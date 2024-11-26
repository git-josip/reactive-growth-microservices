package com.reactive.recommendation.module.transaction.event.suscriber

import com.reactive.recommendation.module.transaction.event.domain.TransactionCreatedEvent
import com.reactive.recommendation.common.events.publisher.IEventPubService
import com.reactive.recommendation.common.events.subscriber.EvenSubService
import com.reactive.recommendation.module.transaction.mapper.toTransactionCreate
import com.reactive.recommendation.module.transaction.service.ITransactionService
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