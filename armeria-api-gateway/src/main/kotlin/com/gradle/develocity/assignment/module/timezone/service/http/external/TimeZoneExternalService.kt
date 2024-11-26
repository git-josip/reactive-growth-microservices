package com.gradle.develocity.assignment.module.timezone.service.http.external

import com.gradle.develocity.assignment.common.configuration.ObjectMapperConfiguration
import com.gradle.develocity.assignment.common.exception.handler.ValidationExceptionHandler
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.convert
import com.gradle.develocity.assignment.common.webclient.RestClientFactory
import com.gradle.develocity.assignment.module.timezone.contract.http.external.response.GetTimeZonePaginated
import com.gradle.develocity.assignment.module.timezone.dto.request.GetTimezones
import com.gradle.develocity.assignment.module.timezone.dto.response.Timezones
import com.gradle.develocity.assignment.module.timezone.mapper.toTimeZones
import com.gradle.develocity.assignment.module.timezone.validator.GetTimezonesValidator
import com.linecorp.armeria.client.kotlin.execute
import com.linecorp.armeria.common.HttpData
import com.linecorp.armeria.common.HttpHeaderNames
import com.linecorp.armeria.common.HttpHeaders
import com.linecorp.armeria.common.HttpObject
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.ResponseHeaders
import com.linecorp.armeria.server.annotation.*
import io.netty.handler.codec.http.HttpHeaderValues
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory


@ExceptionHandler(ValidationExceptionHandler::class)
class TimeZoneExternalService {
    @Get
    @ProducesJson
    @ConsumesJson
    @MatchesHeader("accept=application/json")
    @Description("Retrieves timezones for provided city list.")
    suspend fun getTimezones(
        getTimezonesRequest: GetTimezones
    ): Timezones {
        getTimeZoneValidator.validate(getTimezonesRequest).failOnError()
        val citiesParsed = getTimezonesRequest.city.split(TIMEZONE_DB_CITY_PARAM_SEPARATOR).map { it.trim() }.toSet()

        return coroutineScope {
            val rawTimezones = citiesParsed.map {
                async {
                    getTimezonesForCity(city = it)
                }
            }.awaitAll()

            val timezones = async { rawTimezones.map { it.toTimeZones() } }.await()
            val uniqueIntersection = async {
                timezones.flatMap { it.cities }.distinctBy { it.uniqueIdentifier }
            }.await()

            Timezones(
                success = true,
                cities = uniqueIntersection
            )
        }
    }

    @Get
//    @ProducesJson
//    @MatchesHeader("accept=application/json")
    @Description("Retrieves timezones for provided city list.")
    @Path("/city/stream")
    @Produces("application/json-seq")
    suspend fun getTimezonesCityStream(
        getTimezonesRequest: GetTimezones
    ): HttpResponse {
        getTimeZoneValidator.validate(getTimezonesRequest).failOnError()
        val citiesParsed = getTimezonesRequest.city.split(TIMEZONE_DB_CITY_PARAM_SEPARATOR).map { it.trim() }.toSet()

        return coroutineScope {
            val rawTimezones = citiesParsed.map {
                async {
                    getTimezonesForCity(city = it)
                }
            }.awaitAll()

            val timezones = async { rawTimezones.map { it.toTimeZones() } }.await()
            val uniqueIntersection = async {
                timezones.flatMap { it.cities }.distinctBy { it.uniqueIdentifier }
            }.await()

            val dataStream = Observable.just(uniqueIntersection);
            val httpDataStream = async {  dataStream
                .map { ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(it) }
                .map(HttpData::ofUtf8) }.await()

            val httpHeaders = ResponseHeaders.builder()
                .status(HttpStatus.OK)
                .add(HttpHeaders.of(HttpHeaderNames.CONTENT_TYPE, "application/json-seq"))
                .build()

            val responseStream: Observable<HttpObject> = async {
                Observable.concat(Observable.just<ResponseHeaders>(httpHeaders), httpDataStream)
            }.await()

            HttpResponse.of(responseStream.toFlowable(BackpressureStrategy.BUFFER))
        }
    }

    private suspend fun getTimezonesForCity(city: String): GetTimeZonePaginated {
        return coroutineScope {
            withContext(timezoneRestDispatcher) {
                val firstPage = async { getTimezonesForCity(city = city, page = 1) }.await()

                val remainingPages = if(firstPage.totalPage > 1) {
                    (SECOND_PAGE..firstPage.totalPage).map { currentPage ->
                        async {
                            getTimezonesForCity(city = city, page = currentPage) }
                    }.awaitAll()
                } else listOf()

                firstPage.copy(zones = firstPage.zones + remainingPages.flatMap { it.zones })
            }
        }
    }

    private suspend fun getTimezonesForCity(city: String, page: Int): GetTimeZonePaginated {
        require(page > 0) {"page must be greater than 0"}

        return coroutineScope {
            withContext(timezoneRestDispatcher) {
                async {
                    if(REQUEST_DEBUG) {
                        log.debug("########## PAGINATION getTimezonesForCity: $city, $page")
                    }
                    restClient.get("$TIMEZONE_DB_URL/v2.1/get-time-zone?key=$TIMEZONE_DB_API_KEY&format=json&by=city&city=${city}&country=US&fields=cityName,regionName,abbreviation,formatted,gmtOffset&page=$page")
                        .execute<GetTimeZonePaginated>(ObjectMapperConfiguration.jacksonObjectMapper)
                }.await().content()
            }
        }
    }

    companion object {
        private const val SECOND_PAGE = 2

        val timezoneRestDispatcher = Dispatchers.IO.limitedParallelism(200, "timezoneRestDispatcher")

        private val TIMEZONE_DB_URL = ApplicationPropertiesUtils.getProperty("timezone.db.url")
        private val TIMEZONE_DB_API_KEY = ApplicationPropertiesUtils.getProperty("timezone.db.api-key")
        private val TIMEZONE_DB_REST_CLIENT_CONCURRENCY_LIMIT = ApplicationPropertiesUtils.getProperty("timezone.db.rest-client.concurrency-limit").convert<Int>()
        private val TIMEZONE_DB_CITY_PARAM_SEPARATOR = ApplicationPropertiesUtils.getProperty("timezone.db.city-param-separator")

        private val restClient = RestClientFactory.of("armeria.timezonesdb.client", TIMEZONE_DB_REST_CLIENT_CONCURRENCY_LIMIT)
        private val log = LoggerFactory.getLogger(this::class.java)
        private val REQUEST_DEBUG = ApplicationPropertiesUtils.getProperty("request.debug").convert<Boolean>()

        private val getTimeZoneValidator = GetTimezonesValidator()
    }
}
