package com.reactive.product.module.transaction.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class TransactionCreateRequest(
    @JsonProperty(value = "fromAcc", required = true)
    val fromAcc: String,

    @JsonProperty(value = "toAcc", required = true)
    val toAcc: String,

    @JsonProperty(value = "amount", required = true)
    val amount: BigDecimal
)

