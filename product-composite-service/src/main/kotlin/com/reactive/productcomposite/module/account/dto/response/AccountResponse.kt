package com.reactive.productcomposite.module.account.dto.response

import java.math.BigDecimal

data class AccountResponse(
    val id: String,
    val balance: BigDecimal
)
