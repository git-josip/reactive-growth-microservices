package com.gradle.develocity.assignment.module.timezone.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

data class Timezones(
    val success: Boolean,
    @JsonInclude(Include.NON_NULL) val errorMsg: String? = null,
    val cities: List<TimeZonesCity>
)

data class TimeZonesCity(
    val city: String,
    val state: String?,
    val time: String,
    val zone: String,
    val utcOffset: Int
) {
    @JsonIgnore
    val uniqueIdentifier = "${city.filterNot { it.isWhitespace() }}-$state-$zone-$utcOffset"
}