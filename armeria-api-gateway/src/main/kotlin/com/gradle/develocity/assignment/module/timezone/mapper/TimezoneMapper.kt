package com.gradle.develocity.assignment.module.timezone.mapper

import com.gradle.develocity.assignment.common.utils.DateUtils
import com.gradle.develocity.assignment.module.timezone.contract.http.external.response.GetTimeZonePaginated
import com.gradle.develocity.assignment.module.timezone.contract.http.external.response.TimeZoneCity
import com.gradle.develocity.assignment.module.timezone.dto.response.TimeZonesCity
import com.gradle.develocity.assignment.module.timezone.dto.response.Timezones

fun TimeZoneCity.toTimeZonesCity(): TimeZonesCity {
    return TimeZonesCity(
        city = this.cityName,
        state = this.regionName,
        zone = this.abbreviation,
        time = this.formatted,
        utcOffset = this.gmtOffset / DateUtils.SECONDS_IN_DAY.toInt()
    )
}

fun GetTimeZonePaginated.toTimeZones(): Timezones {
    return Timezones(
        success = true,
        cities = this.zones.map { it.toTimeZonesCity() }
    )
}