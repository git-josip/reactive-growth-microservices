package com.reactive.order.module.transaction.service

import com.reactive.order.common.exception.NotFoundException
import com.reactive.order.module.transaction.domain.Transaction
import com.reactive.order.module.transaction.domain.TransactionCreate
import java.util.*

interface ITransactionService {
    suspend fun tryGetTransactionById(id: UUID): Transaction?
    suspend fun createTransaction(transactionCreate: TransactionCreate): Transaction
    suspend fun publishTransaction(transactionCreate: TransactionCreate)

    suspend fun getTransactionById(id: UUID): Transaction {
        return tryGetTransactionById(id) ?: throw NotFoundException("Transaction with id '$id' does not exist")
    }
}