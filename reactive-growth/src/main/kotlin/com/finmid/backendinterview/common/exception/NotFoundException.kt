package com.finmid.backendinterview.common.exception

open class NotFoundException(override val message: String = "Not Found") : RuntimeException(message)
