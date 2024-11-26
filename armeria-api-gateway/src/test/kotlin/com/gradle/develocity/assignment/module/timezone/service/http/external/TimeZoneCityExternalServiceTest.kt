package com.gradle.develocity.assignment.module.timezone.service.http.external

import com.gradle.develocity.assignment.common.configuration.ObjectMapperConfiguration
import com.gradle.develocity.assignment.common.configuration.ServerConfiguration
import com.gradle.develocity.assignment.common.exception.response.ErrorResponse
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.convert
import com.gradle.develocity.assignment.common.validation.ValidationError
import com.gradle.develocity.assignment.common.webclient.RestClientFactory
import com.gradle.develocity.assignment.module.timezone.dto.response.Timezones
import com.gradle.develocity.assignment.module.timezone.validator.GetTimezonesValidator
import com.linecorp.armeria.common.HttpHeaderNames
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.server.ServerBuilder
import com.linecorp.armeria.testing.junit5.server.ServerExtension
import io.netty.handler.codec.http.HttpHeaderValues
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.*

@Execution(ExecutionMode.CONCURRENT)
class TimeZoneCityExternalServiceTest {
    @Test
    fun `providing valid unique US city api call should response with single US city`() {
        val response = client
            .get("${server.httpUri()}/timezones?city=Yosemite Valley")
            .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
            .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
            .execute(Timezones::class.java)
            .join()

        assertEquals(HttpStatus.OK, response.status())
        assertEquals(1, response.content().cities.size)
        assertTrue { response.content().success }
        assertNull(response.content().errorMsg)

        assertEquals(
            "Yosemite Valley",
            response.content().cities.first().city
        )
        assertEquals(
            "California",
            response.content().cities.first().state
        )
        assertEquals(
            "PDT",
            response.content().cities.first().zone
        )
        assertEquals(
            -7,
            response.content().cities.first().utcOffset
        )
    }

    @Test
    fun `providing duplicate city in csv should return unique results`() {
        val response = client
            .get("${server.httpUri()}/timezones?city=Yosemite Valley,Yosemite Valley,Yosemite Valley,Yosemite Valley")
            .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
            .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
            .execute(Timezones::class.java)
            .join()

        assertEquals(HttpStatus.OK, response.status())
        assertEquals(1, response.content().cities.size)
        assertTrue { response.content().success }
        assertNull(response.content().errorMsg)

        assertEquals(
            "Yosemite Valley",
            response.content().cities.first().city
        )
        assertEquals(
            "California",
            response.content().cities.first().state
        )
        assertEquals(
            "PDT",
            response.content().cities.first().zone
        )
        assertEquals(
            -7,
            response.content().cities.first().utcOffset
        )
    }

    @ParameterizedTest
    @MethodSource("uniqueCities")
    fun `providing unique match US city api call should response with unique US city`(
        cityParam: String,
        expectedValuesSize: Int
    ) {
        apiExecutorValidateResponseSizeHelper(cityParam, expectedValuesSize)
    }

    @ParameterizedTest
    @MethodSource("multiMatchCities")
    fun `providing valid multi match US city api call should response with array of US cities`(
        cityParam: String,
        expectedValuesSize: Int
    ) {
        apiExecutorValidateResponseSizeHelper(cityParam, expectedValuesSize)
    }

    @ParameterizedTest
    @MethodSource("duplicateMultiMatchCities")
    fun `providing duplicate multi match US city api call should response with unique array of US cities`(
        cityParam: String,
        expectedValuesSize: Int
    ) {
        apiExecutorValidateResponseSizeHelper(cityParam, expectedValuesSize)
    }

    @ParameterizedTest
    @MethodSource("wildcardCities")
    fun `providing valid wildcard US city api call should response with array of US cities if search matches any`(
        cityParam: String,
        expectedValuesSize: Int
    ) {
        apiExecutorValidateResponseSizeHelper(cityParam, expectedValuesSize)
    }

    @ParameterizedTest
    @MethodSource("listOfValidCities")
    fun `providing valid list of US cities api call should response with array of all US cities provided if search matches any`(
        cityParam: String,
        expectedValuesSize: Int
    ) {
        apiExecutorValidateResponseSizeHelper(cityParam, expectedValuesSize)
    }

    @ParameterizedTest
    @MethodSource("listOfNonExistingCities")
    fun `providing non existing list of US cities api call should response with empty array`(
        cityParam: String,
        expectedValuesSize: Int
    ) {
        apiExecutorValidateResponseSizeHelper(cityParam, expectedValuesSize)
    }

    @Test
    fun `not specifying access application_json header should result in Http 404 Not Found status`() {
        client
            .get("${server.httpUri()}/timezones?city=Denver")
            .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
            .execute { res ->
                val response = res.aggregate().join()

                assertEquals(HttpStatus.NOT_FOUND, response.status())
            }
    }

    @ParameterizedTest
    @MethodSource("listOfBlankCities")
    fun `providing blank city should result in Bad Request response`(cityParam: String) {
        apiExecutorValidationErrorHelper(
            cityParam = cityParam,
            expectedErrors = listOf(
                ValidationError(
                    field = GetTimezonesValidator.FIELD_NAME,
                    message = "Must not be empty"
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("emptyCityInCsv")
    fun `if provided csv city contains any blank api call should fail`(cityParam: String) {
        apiExecutorValidationErrorHelper(
            cityParam = cityParam,
            expectedErrors = listOf(
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[2]",
                    message = "Must not be empty"
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("allEmptyCitiesCsv")
    fun `if provided csv city are all blank api call should fail`(cityParam: String) {
        apiExecutorValidationErrorHelper(
            cityParam = cityParam,
            expectedErrors = listOf(
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[0]",
                    message = "Must not be empty"
                ),
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[1]",
                    message = "Must not be empty"
                ),
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[2]",
                    message = "Must not be empty"
                ),
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[3]",
                    message = "Must not be empty"
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("moreCitiesThanAllowed")
    fun `providing number of cities which exceeds max allowed number of cities api call should fail`(cityParam: String) {
        apiExecutorValidationErrorHelper(
            cityParam = cityParam,
            expectedErrors = listOf(
                ValidationError(
                    field = GetTimezonesValidator.FIELD_NAME,
                    message = "The number of cities exceeds the maximum allowed limit of $TIMEZONE_DB_CITY_LIMIT"
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("wildcardSearchCityLessThan5Characters")
    fun `providing wildcard search city which has less than 5 characters api call should fail`(cityParam: String) {
        apiExecutorValidationErrorHelper(
            cityParam = cityParam,
            expectedErrors = listOf(
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[3]",
                    message = "Minimum 5 characters needed for wildcard search."
                )
            )
        )
    }

    @Test
    fun `executing more requests that rate limit should fail with Too Many Requests error`() {
        (1..3).forEach {
            client
                .get("${serverWithLowRateLimit.httpUri()}/timezones?city=Denver")
                .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
                .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
                .execute { res ->
                    val response = res.aggregate().join()
                    assertEquals(HttpStatus.OK, response.status())
                }
        }

        client
            .get("${serverWithLowRateLimit.httpUri()}/timezones?city=Denver")
            .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
            .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
            .execute { _ ->

            }

        client
            .get("${serverWithLowRateLimit.httpUri()}/timezones?city=Denver")
            .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
            .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
            .execute { res ->
                val response = res.aggregate().join()
                assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.status())
            }
    }

    companion object {
        private val TIMEZONE_DB_CITY_LIMIT = ApplicationPropertiesUtils.getProperty("timezone.db.city.limit").convert<Int>()
        private val SERVER_API_KEY = ApplicationPropertiesUtils.getProperty("server.api-key")

        @RegisterExtension
        val server: ServerExtension = object : ServerExtension() {
            @Throws(Exception::class)
            override fun configure(serverBuilder: ServerBuilder) {
                ServerConfiguration.configureServices(
                    serverBuilder = serverBuilder,
                    rateLimit = 500
                )
            }
        }

        @RegisterExtension
        val serverWithLowRateLimit: ServerExtension = object : ServerExtension() {
            @Throws(Exception::class)
            override fun configure(serverBuilder: ServerBuilder) {
                ServerConfiguration.configureServices(
                    serverBuilder = serverBuilder,
                    rateLimit = 3
                )
            }
        }

        private fun apiExecutorValidateResponseSizeHelper(
            cityParam: String,
            expectedValuesSize: Int
        ) {
            val response = client
                .get("${server.httpUri()}/timezones?city=$cityParam")
                .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
                .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
                .execute(Timezones::class.java)
                .join()

            assertEquals(HttpStatus.OK, response.status())
            assertEquals(expectedValuesSize, response.content().cities.size)
            assertNull(response.content().errorMsg)
        }

        private fun apiExecutorValidationErrorHelper(
            cityParam: String,
            expectedErrors: List<ValidationError>
        ) {
            client
                .get("${server.httpUri()}/timezones?city=$cityParam")
                .header(HttpHeaderNames.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
                .header(HttpHeaderNames.AUTHORIZATION.toString(), "apikey $SERVER_API_KEY")
                .execute { res ->
                    val response = res.aggregate().join()

                    assertEquals(HttpStatus.BAD_REQUEST, response.status())

                    val errorResponse = ObjectMapperConfiguration.jacksonObjectMapper.readValue(
                        response.contentUtf8().toString(),
                        ErrorResponse::class.java
                    )

                    assertFalse { errorResponse.success }
                    assertEquals("Validation Exception", errorResponse.errorMsg)
                    assertEquals(expectedErrors, errorResponse.errors)
                }
        }

        val client = RestClientFactory.of("test-rest", 50)

        @JvmStatic
        fun uniqueCities() = listOf(
            Arguments.of("Yosemite Valley", 1),
            Arguments.of("Rapid City Regional Airport", 1),
        )

        @JvmStatic
        fun multiMatchCities() = listOf(
            Arguments.of("Denver", 4),
            Arguments.of("Oakland", 15)
        )

        @JvmStatic
        fun duplicateMultiMatchCities() = listOf(
            Arguments.of("Denver,Denver,Denver,Denver", 4),
            Arguments.of("Oakland,Oakland,Oakland,Oakland", 15),
            Arguments.of("* town*,* town*,* town*", 25),
            Arguments.of("Oakland,*Athens*,Oakland,*Athens*", 30)
        )

        @JvmStatic
        fun wildcardCities() = listOf(
            Arguments.of("*Athens*", 15),
            Arguments.of("* town*", 25)
        )

        @JvmStatic
        fun listOfValidCities() = listOf(
            Arguments.of("Denver,Oakland", 19),
            Arguments.of("Oakland,*Athens*", 30)
        )

        @JvmStatic
        fun listOfNonExistingCities() = listOf(
            Arguments.of("Isengaard", 0),
            Arguments.of("Split", 0),
        )

        @JvmStatic
        fun listOfBlankCities() = listOf(
            Arguments.of("    "),
            Arguments.of(" "),
            Arguments.of(""),
        )

        @JvmStatic
        fun emptyCityInCsv() = listOf(
            Arguments.of("Denver,Chicago,  ,New York"),
            Arguments.of("Athens,City,  ,New York")
        )

        @JvmStatic
        fun allEmptyCitiesCsv() = listOf(
            Arguments.of(",,  ,"),
            Arguments.of(",      ,  ,")
        )

        @JvmStatic
        fun moreCitiesThanAllowed() = listOf(
            Arguments.of("Denver,Chicago,New York,Appleton City,Benton City,Boise City"),
        )

        @JvmStatic
        fun wildcardSearchCityLessThan5Characters() = listOf(
            Arguments.of("Denver,Chicago,New York,App*"),
            Arguments.of("Denver,Chicago,New York,*App*"),
            Arguments.of("Denver,Chicago,New York,**App***"),
        )
    }
}