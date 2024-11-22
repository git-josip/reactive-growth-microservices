package com.finmid.backendinterview.module.transaction.event.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.finmid.backendinterview.common.events.domain.BaseMutexEvent
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionCreatedEvent(
    val fromAcc: String,
    val toAcc: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime
): BaseMutexEvent {
    @get:JsonIgnore
    override val mutexKey: String
        get() = fromAcc
}