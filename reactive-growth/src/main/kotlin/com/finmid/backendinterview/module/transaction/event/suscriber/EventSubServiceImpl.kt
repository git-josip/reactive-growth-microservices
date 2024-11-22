package com.finmid.backendinterview.module.transaction.event.suscriber

import com.finmid.backendinterview.module.transaction.event.domain.TransactionCreatedEvent
import com.finmid.backendinterview.common.events.publisher.IEventPubService
import com.finmid.backendinterview.common.events.subscriber.EvenSubService
import com.finmid.backendinterview.module.transaction.mapper.toTransactionCreate
import com.finmid.backendinterview.module.transaction.service.ITransactionService
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