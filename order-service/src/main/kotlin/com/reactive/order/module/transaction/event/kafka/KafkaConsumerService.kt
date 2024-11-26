package com.reactive.order.module.transaction.event.kafka

import com.reactive.order.common.configuration.ObjectMapperConfiguration
import com.reactive.order.module.transaction.event.domain.TransactionCreatedEvent
import com.reactive.order.module.transaction.mapper.toTransactionCreate
import com.reactive.order.module.transaction.service.ITransactionService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(
    private val transactionService: ITransactionService
) {
    @KafkaListener(topics = ["transactions"], groupId = "account-service-group")
    suspend fun listen(record: ConsumerRecord<String, String>) {
        val tx = ObjectMapperConfiguration.jacksonObjectMapper.readValue(record.value(), TransactionCreatedEvent::class.java)
        println("XXXXXXXXX => $tx")
        transactionService.createTransaction(tx.toTransactionCreate())
    }

}