package com.reactive.recommendation.module.transaction.mapper

import com.reactive.recommendation.database.jooq.tables.records.TransactionsRecord
import com.reactive.recommendation.module.transaction.domain.Transaction
import com.reactive.recommendation.module.transaction.domain.TransactionCreate
import com.reactive.recommendation.module.transaction.dto.request.TransactionCreateRequest
import com.reactive.recommendation.module.transaction.dto.response.TransactionResponse
import com.reactive.recommendation.module.transaction.event.domain.TransactionCreatedEvent
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun  TransactionsRecord.toTransaction(): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        fromAcc = this.fromAcc,
        toAcc = this.toAcc,
        createdAt = this.createdAt!!
    )
}

fun TransactionCreate.toTransactionsRecord(txId: UUID): TransactionsRecord {
    return TransactionsRecord(
        id = txId,
        fromAcc = this.fromAcc,
        toAcc = this.toAcc,
        amount = this.amount,
        createdAt = this.createdAt
    )
}

fun TransactionCreateRequest.toTransactionCreate(): TransactionCreate {
    return TransactionCreate(
        toAcc = this.toAcc,
        fromAcc = this.fromAcc,
        amount = this.amount,
        createdAt = LocalDateTime.now(ZoneId.of("UTC"))
    )
}

fun Transaction.toTransactionResponse(): TransactionResponse {
    return TransactionResponse(
        id = this.id,
        fromAcc = this.fromAcc,
        toAcc = this.toAcc,
        amount = this.amount,
        createdAt = this.createdAt
    )
}

fun TransactionCreate.toTransactionCreatedEvent(): TransactionCreatedEvent {
    return TransactionCreatedEvent(
        fromAcc = this.fromAcc,
        toAcc = this.toAcc,
        amount = this.amount,
        createdAt = LocalDateTime.now(ZoneId.of("UTC"))
    )
}

fun TransactionCreatedEvent.toTransactionCreate(): TransactionCreate {
    return TransactionCreate(
        fromAcc = this.fromAcc,
        toAcc = this.toAcc,
        amount = this.amount,
        createdAt = this.createdAt
    )
}