package com.reactive.inventory.module.account.dto.request

import java.math.BigDecimal

data class AccountCreateRequest(
    val id: String,
    val balance: BigDecimal
)
