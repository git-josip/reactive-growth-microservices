package com.reactive.productcomposite.module.transaction.mapper

import com.reactive.productcomposite.database.jooq.tables.records.TransactionsRecord
import com.reactive.productcomposite.module.transaction.domain.Transaction
import com.reactive.productcomposite.module.transaction.domain.TransactionCreate
import com.reactive.productcomposite.module.transaction.dto.request.TransactionCreateRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

@Execution(ExecutionMode.CONCURRENT)
class TransactionMapperTest {
    @Test
    fun `TransactionsRecord should be successfully mapped to Transaction`() {
        val source = TransactionsRecord(
            id = UUID.randomUUID(),
            amount = BigDecimal(100),
            fromAcc = "account1",
            toAcc = "account2",
            createdAt = LocalDateTime.now()
        )

        val mapped = source.toTransaction()

        assertEquals(source.id, mapped.id)
        assertEquals(source.amount, mapped.amount)
        assertEquals(source.fromAcc, mapped.fromAcc)
        assertEquals(source.toAcc, mapped.toAcc)
        assertEquals(source.createdAt, mapped.createdAt)
    }

    @Test
    fun `TransactionCreate should be successfully mapped to TransactionsRecord`() {
        val source = TransactionCreate(
            fromAcc = "account1",
            toAcc = "account2",
            amount = BigDecimal(200),
            createdAt = LocalDateTime.now()
        )

        val txId = UUID.randomUUID()
        val mapped = source.toTransactionsRecord(txId)

        assertEquals(txId, mapped.id)
        assertEquals(source.fromAcc, mapped.fromAcc)
        assertEquals(source.toAcc, mapped.toAcc)
        assertEquals(source.amount, mapped.amount)
    }

    @Test
    fun `TransactionCreateRequest should be successfully mapped to TransactionCreate`() {
        val source = TransactionCreateRequest(
            fromAcc = "account1",
            toAcc = "account2",
            amount = BigDecimal(200)
        )

        val mapped = source.toTransactionCreate()

        assertEquals(source.fromAcc, mapped.fromAcc)
        assertEquals(source.toAcc, mapped.toAcc)
        assertEquals(source.amount, mapped.amount)
    }

    @Test
    fun `Transaction should be successfully mapped to TransactionResponse`() {
        val source = Transaction(
            id = UUID.randomUUID(),
            amount = BigDecimal(100),
            fromAcc = "account1",
            toAcc = "account2",
            createdAt = LocalDateTime.now()
        )

        val mapped = source.toTransactionResponse()

        assertEquals(source.id, mapped.id)
        assertEquals(source.amount, mapped.amount)
        assertEquals(source.fromAcc, mapped.fromAcc)
        assertEquals(source.toAcc, mapped.toAcc)
        assertEquals(source.createdAt, mapped.createdAt)
    }
}