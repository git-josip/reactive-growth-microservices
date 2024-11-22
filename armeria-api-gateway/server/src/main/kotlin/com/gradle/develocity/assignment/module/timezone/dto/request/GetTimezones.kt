package com.gradle.develocity.assignment.module.timezone.dto.request

import com.linecorp.armeria.server.annotation.Description
import com.linecorp.armeria.server.annotation.Param

data class GetTimezones(
    @Description("list of cities provided as comma separated values list")
    @Param("city") val city: String
)