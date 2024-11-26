package com.reactive.recommendation.common.exception

open class NotFoundException(override val message: String = "Not Found") : RuntimeException(message)
