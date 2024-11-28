package com.reactive.order.common.exception

open class NotFoundException(override val message: String = "Not Found") : RuntimeException(message)