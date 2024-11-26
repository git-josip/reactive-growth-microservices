package com.reactive.review.module.account.domain

import java.math.BigDecimal

data class Account(
    val id: String,
    val balance: BigDecimal,
    val version: Long
)
