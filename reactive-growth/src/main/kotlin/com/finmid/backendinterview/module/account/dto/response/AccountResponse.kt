package com.finmid.backendinterview.module.account.dto.response

import java.math.BigDecimal

data class AccountResponse(
    val id: String,
    val balance: BigDecimal
)
