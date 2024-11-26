package com.reactive.productcomposite.module.account.mapper

import com.reactive.productcomposite.database.jooq.tables.records.AccountsRecord
import com.reactive.productcomposite.module.account.domain.Account
import com.reactive.productcomposite.module.account.domain.AccountCreate
import com.reactive.productcomposite.module.account.dto.request.AccountCreateRequest
import com.reactive.productcomposite.module.account.dto.response.AccountResponse

fun AccountsRecord.toAccount(): Account {
    return Account(
        id = this.id,
        balance = this.balance,
        version = this.version
    )
}

fun Account.toAccountResponse(): AccountResponse {
    return AccountResponse(
        id = this.id,
        balance = this.balance
    )
}

fun AccountCreateRequest.toAccountCreate(): AccountCreate {
    return AccountCreate(
        id = this.id,
        balance = this.balance,
        version = 0
    )
}

fun AccountCreate.toAccountsRecord(): AccountsRecord {
    return AccountsRecord(
        id = this.id,
        balance = this.balance,
        version = this.version
    )
}
