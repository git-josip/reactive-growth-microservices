package com.finmid.backendinterview.module.account.domain

import java.math.BigDecimal

data class AccountCreate(
    val id: String,
    val balance: BigDecimal,
    val version: Long
)
