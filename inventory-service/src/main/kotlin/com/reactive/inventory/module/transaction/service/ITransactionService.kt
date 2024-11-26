package com.reactive.inventory.module.transaction.service

import com.reactive.inventory.common.exception.NotFoundException
import com.reactive.inventory.module.transaction.domain.Transaction
import com.reactive.inventory.module.transaction.domain.TransactionCreate
import java.util.*

interface ITransactionService {
    suspend fun tryGetTransactionById(id: UUID): Transaction?
    suspend fun createTransaction(transactionCreate: TransactionCreate): Transaction
    suspend fun publishTransaction(transactionCreate: TransactionCreate)

    suspend fun getTransactionById(id: UUID): Transaction {
        return tryGetTransactionById(id) ?: throw NotFoundException("Transaction with id '$id' does not exist")
    }
}