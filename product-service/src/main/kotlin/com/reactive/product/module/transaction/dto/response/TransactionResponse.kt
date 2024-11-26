package com.reactive.product.module.transaction.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val fromAcc: String,
    val toAcc: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime
)

