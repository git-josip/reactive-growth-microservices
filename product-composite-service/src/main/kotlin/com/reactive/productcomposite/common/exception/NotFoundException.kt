package com.reactive.productcomposite.common.exception

open class NotFoundException(override val message: String = "Not Found") : RuntimeException(message)
