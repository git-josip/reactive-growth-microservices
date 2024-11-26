package com.reactive.order.module.transaction.event.suscriber

import com.reactive.order.module.transaction.event.domain.TransactionCreatedEvent
import com.reactive.order.common.events.publisher.IEventPubService
import com.reactive.order.common.events.subscriber.EvenSubService
import com.reactive.order.module.transaction.mapper.toTransactionCreate
import com.reactive.order.module.transaction.service.ITransactionService
import org.springframework.stereotype.Service

@Service
class EventSubServiceImpl(
    eventPubService: IEventPubService<TransactionCreatedEvent>,
    private val transactionService: ITransactionService
): EvenSubService<TransactionCreatedEvent>(eventPubService.events) {
    override suspend fun handleEvent(event: TransactionCreatedEvent) {
        transactionService.createTransaction(event.toTransactionCreate())
    }
}