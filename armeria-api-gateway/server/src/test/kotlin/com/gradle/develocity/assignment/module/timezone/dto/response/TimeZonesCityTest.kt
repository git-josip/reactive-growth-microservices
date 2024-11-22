package com.gradle.develocity.assignment.module.timezone.dto.response

import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

@Execution(ExecutionMode.CONCURRENT)
class TimeZonesCityTest {
    @ParameterizedTest
    @MethodSource("timezonesResponseWithCities")
    fun `uniqueIdentifier should be correctly calculated`(item: TimeZonesCity) {
        assertEquals(
            "${item.city.filterNot { it.isWhitespace() }}-${item.state}-${item.zone}-${item.utcOffset}",
            item.uniqueIdentifier
        )
    }

    companion object {
        @JvmStatic
        fun timezonesResponseWithCities() = listOf(
            Arguments.of(
                TimeZonesCity(
                    city = "Boger City",
                    state = "North Carolina",
                    time = "2024-10-21 04:10:56",
                    zone = "EDT",
                    utcOffset = -4,
                )
            ),
            Arguments.of(
                TimeZonesCity(
                    city = "Boulder City Municipal Airport",
                    state = "Nevada",
                    time = "2024-10-21 01:10:56",
                    zone = "PDT",
                    utcOffset = -7,
                )
            )
        )
    }
}