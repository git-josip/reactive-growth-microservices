package com.reactive.order.common.exception

open class AccountStateChangedException(override val message: String = "Account state has changed.") : RuntimeException(message)
