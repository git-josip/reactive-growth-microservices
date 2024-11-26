package com.reactive.inventory.common.exception

open class AccountStateChangedException(override val message: String = "Account state has changed.") : RuntimeException(message)
