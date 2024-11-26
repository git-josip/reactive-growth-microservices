package com.reactive.product.common.exception

open class AccountStateChangedException(override val message: String = "Account state has changed.") : RuntimeException(message)
