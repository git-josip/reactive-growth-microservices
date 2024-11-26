package com.reactive.order.common.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ObjectMapperConfiguration {
    val jacksonObjectMapper = jacksonObjectMapper()

    init {
        jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        jacksonObjectMapper.registerModule(JavaTimeModule())
    }
}