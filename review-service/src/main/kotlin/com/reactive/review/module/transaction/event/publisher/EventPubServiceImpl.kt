package com.reactive.review.module.transaction.event.publisher

import com.reactive.review.common.events.publisher.IEventPubService
import com.reactive.review.module.transaction.event.domain.TransactionCreatedEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.springframework.stereotype.Service

@Service
class EventPubServiceImpl : IEventPubService<TransactionCreatedEvent> {
    private val _events = MutableSharedFlow<TransactionCreatedEvent>(replay = 0)

    final override val events: SharedFlow<TransactionCreatedEvent> = _events.asSharedFlow()

    override suspend fun publishEvent(event: TransactionCreatedEvent) {
        _events.emit(event)
    }
}