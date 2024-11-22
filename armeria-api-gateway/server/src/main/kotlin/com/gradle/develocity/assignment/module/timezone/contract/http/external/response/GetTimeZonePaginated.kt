package com.gradle.develocity.assignment.module.timezone.contract.http.external.response

data class GetTimeZonePaginated(
    val status: String,
    val message: String,
    val totalPage: Int,
    val currentPage: Int,
    val zones: List<TimeZoneCity>
)

data class TimeZoneCity(
    val regionName: String? = null,
    val cityName: String,
    val abbreviation: String,
    val gmtOffset: Int,
    val formatted: String
)