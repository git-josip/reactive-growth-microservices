package com.reactive.product.module

import com.reactive.product.common.events.publisher.IEventPubService
import com.reactive.product.module.transaction.domain.TransactionCreate
import com.reactive.product.module.transaction.event.domain.TransactionCreatedEvent
import com.reactive.product.module.transaction.event.publisher.EventPubServiceImpl
import com.reactive.product.module.transaction.event.suscriber.EventSubServiceImpl
import com.reactive.product.module.transaction.mapper.toTransactionCreate
import com.reactive.product.module.transaction.service.ITransactionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class EventSubServiceImplTest {

    private val eventPubService: IEventPubService<TransactionCreatedEvent> = mock()
    private val transactionService: ITransactionService = mock()
    private val eventsFlow = MutableSharedFlow<TransactionCreatedEvent>(replay = 0)
    private val eventsFlowShared =  eventsFlow.asSharedFlow()

    init {
        // Mock the events flow
        whenever(eventPubService.events).thenReturn(eventsFlowShared)
        runBlocking {
            whenever(eventPubService.publishEvent(any())).then {
                runBlocking {
                    eventsFlow.emit(it.getArgument(0, TransactionCreatedEvent::class.java))
                }
            }
        }
    }

    @Test
    fun `test handleEvent`() = runTest {
        EventSubServiceImpl(eventPubService, transactionService)
        verify(eventPubService).events

        val event = TransactionCreatedEvent("fromAcc", "toAcc", 100.toBigDecimal(), LocalDateTime.now())
        
        val latch = CountDownLatch(1)
        whenever(transactionService.createTransaction(any())).thenAnswer {
            latch.countDown()
            Unit
        }

        eventPubService.publishEvent(event)

        // Wait for the event to be processed
        latch.await(5, TimeUnit.SECONDS)

        // Verify that the event was handled
        verify(transactionService).createTransaction(event.toTransactionCreate())
    }
}