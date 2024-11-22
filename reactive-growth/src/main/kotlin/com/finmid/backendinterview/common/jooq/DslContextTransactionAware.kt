package com.finmid.backendinterview.common.jooq

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine

interface DslContextTransactionAware {
    val dslContext: DSLContext

    suspend fun <T> transactional(block: suspend (Configuration) -> T): T {
        return dslContext.transactionCoroutine { txConfiguration ->
            block(txConfiguration)
        }
    }
}