package com.reactive.product.common.exception

open class NotFoundException(override val message: String = "Not Found") : RuntimeException(message)
