package com.reactive.review.module.transaction.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class Transaction(
    val id: UUID,
    val amount: BigDecimal,
    val fromAcc: String,
    val toAcc: String,
    val createdAt: LocalDateTime
)
