package com.reactive.productcomposite.module.account.mapper

import com.reactive.productcomposite.database.jooq.tables.records.AccountsRecord
import com.reactive.productcomposite.module.account.domain.Account
import com.reactive.productcomposite.module.account.domain.AccountCreate
import com.reactive.productcomposite.module.account.dto.request.AccountCreateRequest
import com.reactive.productcomposite.module.account.mapper.toAccount
import com.reactive.productcomposite.module.account.mapper.toAccountCreate
import com.reactive.productcomposite.module.account.mapper.toAccountResponse
import com.reactive.productcomposite.module.account.mapper.toAccountsRecord
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.math.BigDecimal
import kotlin.test.assertEquals

@Execution(ExecutionMode.CONCURRENT)
class AccountMapperTest {
    @Test
    fun `AccountsRecord should be successfully mapped to Account`() {
        val source = AccountsRecord(
            id = "account1",
            balance = BigDecimal(100),
            version = 10
        )

        val mapped = source.toAccount()

        assertEquals(source.id, mapped.id)
        assertEquals(source.balance, mapped.balance)
        assertEquals(source.version, mapped.version)
    }

    @Test
    fun `Account should be successfully mapped to AccountResponse`() {
        val source = Account(
            id = "account1",
            balance = BigDecimal(100),
            version = 10
        )

        val mapped = source.toAccountResponse()

        assertEquals(source.id, mapped.id)
        assertEquals(source.balance, mapped.balance)
    }

    @Test
    fun `AccountCreateRequest should be successfully mapped to AccountCreate`() {
        val source = AccountCreateRequest(
            id = "account1",
            balance = BigDecimal(100)
        )

        val mapped = source.toAccountCreate()

        assertEquals(source.id, mapped.id)
        assertEquals(source.balance, mapped.balance)
        assertEquals(0, mapped.version)
    }

    @Test
    fun `AccountCreate should be successfully mapped to AccountsRecord`() {
        val source = AccountCreate(
            id = "account1",
            balance = BigDecimal(100),
            version = 10
        )

        val mapped = source.toAccountsRecord()

        assertEquals(source.id, mapped.id)
        assertEquals(source.balance, mapped.balance)
        assertEquals(source.version, mapped.version)
    }
}