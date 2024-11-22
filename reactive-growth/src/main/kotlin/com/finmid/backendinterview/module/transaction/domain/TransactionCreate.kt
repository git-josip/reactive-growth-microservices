package com.finmid.backendinterview.module.transaction.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionCreate(
    val fromAcc: String,
    val toAcc: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime
)
