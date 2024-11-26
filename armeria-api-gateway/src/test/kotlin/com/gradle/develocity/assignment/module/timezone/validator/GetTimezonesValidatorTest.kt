package com.gradle.develocity.assignment.module.timezone.validator

import com.gradle.develocity.assignment.common.exception.ValidationException
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.convert
import com.gradle.develocity.assignment.common.validation.ValidationError
import com.gradle.develocity.assignment.module.timezone.dto.request.GetTimezones
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Execution(ExecutionMode.CONCURRENT)
class GetTimezonesValidatorTest {
    private val getTimezonesValidator = GetTimezonesValidator()

    @ParameterizedTest
    @MethodSource("emptyCity")
    fun `validation should fail if city is blank`(item: GetTimezones) {
        val validationResult = getTimezonesValidator.validate(item)

        assertEquals(
            listOf(
                ValidationError(
                    field = GetTimezonesValidator.FIELD_NAME,
                    message = "Must not be empty"
                )
            ),
            validationResult.errors
        )
    }

    @ParameterizedTest
    @MethodSource("emptyCityInCsv")
    fun `validation should fail if city contains any csv value which is blank`(item: GetTimezones) {
        val validationResult = getTimezonesValidator.validate(item)

        assertEquals(
            listOf(
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[2]",
                    message = "Must not be empty"
                )
            ),
            validationResult.errors
        )
    }

    @ParameterizedTest
    @MethodSource("allEmptyCitiesCsv")
    fun `validation should fail if city contains all csv values which are blank`(item: GetTimezones) {
        val validationResult = getTimezonesValidator.validate(item)

        assertEquals(
            listOf(
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
            ),
            validationResult.errors
        )
    }

    @ParameterizedTest
    @MethodSource("moreCitiesThanAllowed")
    fun `validation should fail if number of cities exceeds max allowed number of cities`(item: GetTimezones) {
        val validationResult = getTimezonesValidator.validate(item)

        assertEquals(
            listOf(
                ValidationError(
                    field = GetTimezonesValidator.FIELD_NAME,
                    message = "The number of cities exceeds the maximum allowed limit of $TIMEZONE_DB_CITY_LIMIT"
                )
            ),
            validationResult.errors
        )
    }

    @ParameterizedTest
    @MethodSource("wildcardSearchCityLessThan5Characters")
    fun `validation should fail if wildcard search city has less than 5 characters`(item: GetTimezones) {
        val validationResult = getTimezonesValidator.validate(item)

        assertEquals(
            listOf(
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[3]",
                    message = "Minimum 5 characters needed for wildcard search."
                )
            ),
            validationResult.errors
        )
    }

    @ParameterizedTest
    @MethodSource("wildcardSearchCityLessThan5Characters")
    fun `validation failOnError should throw exception if errors are present`(item: GetTimezones) {
        val validationResult = getTimezonesValidator.validate(item)
        assertEquals(
            listOf(
                ValidationError(
                    field = "${GetTimezonesValidator.FIELD_NAME}[3]",
                    message = "Minimum 5 characters needed for wildcard search."
                )
            ),
            validationResult.errors
        )

        assertFailsWith<ValidationException>(
            block = {
                validationResult.failOnError()
            }
        )
    }

    companion object {
        private val TIMEZONE_DB_CITY_LIMIT = ApplicationPropertiesUtils.getProperty("timezone.db.city.limit").convert<Int>()

        @JvmStatic
        fun emptyCity() = listOf(
            Arguments.of(GetTimezones(city = "   ")),
            Arguments.of(GetTimezones(city = ""))
        )

        @JvmStatic
        fun emptyCityInCsv() = listOf(
            Arguments.of(GetTimezones(city = "Denver,Chicago,  ,New York"))
        )

        @JvmStatic
        fun allEmptyCitiesCsv() = listOf(
            Arguments.of(GetTimezones(city = ",,  ,")),
            Arguments.of(GetTimezones(city = ",      ,  ,"))
        )

        @JvmStatic
        fun moreCitiesThanAllowed() = listOf(
            Arguments.of(GetTimezones(city = "Denver,Chicago,New York,Appleton City,Benton City,Boise City")),
        )

        @JvmStatic
        fun wildcardSearchCityLessThan5Characters() = listOf(
            Arguments.of(GetTimezones(city = "Denver,Chicago,New York,App*")),
            Arguments.of(GetTimezones(city = "Denver,Chicago,New York,*App*")),
            Arguments.of(GetTimezones(city = "Denver,Chicago,New York,**App***")),
        )
    }
}