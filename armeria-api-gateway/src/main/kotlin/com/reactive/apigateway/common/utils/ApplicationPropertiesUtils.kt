package com.reactive.apigateway.common.utils

import java.util.Properties

object ApplicationPropertiesUtils {
    private const val APPLICATION_PROPERTIES_FILE = "application.properties"

    private val properties = Properties()

    init {
        this::class.java.classLoader.getResourceAsStream(APPLICATION_PROPERTIES_FILE).use {
            properties.load(it)
        }
    }

    fun getProperty(key: String): String = properties.getProperty(key)
}