package com.reactive.order.module.order.event.kafka

import com.reactive.order.common.configuration.ObjectMapperConfiguration
import com.reactive.order.module.order.event.domain.OrderEvent
import com.reactive.order.module.order.mapper.toOrderCreate
import com.reactive.order.module.order.service.IOrderService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaOrdersConsumerService(
    private val orderService: IOrderService,
    private val kafkaProducerService: KafkaProducerService
) {
    @KafkaListener(topics = ["orders"], groupId = "order-service", concurrency = "5")
    suspend fun listen(record: ConsumerRecord<String, String>) {
        val orderEvent = ObjectMapperConfiguration.jacksonObjectMapper.readValue(record.value(), OrderEvent::class.java)

        if(orderEvent.status == "INIT") {
            orderService.create(orderEvent.toOrderCreate())
        }
    }
}