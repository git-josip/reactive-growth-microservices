package com.reactive.productcomposite.module.transaction.event.suscriber

import com.reactive.productcomposite.module.transaction.event.domain.TransactionCreatedEvent
import com.reactive.productcomposite.common.events.publisher.IEventPubService
import com.reactive.productcomposite.common.events.subscriber.EvenSubService
import com.reactive.productcomposite.module.transaction.mapper.toTransactionCreate
import com.reactive.productcomposite.module.transaction.service.ITransactionService
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