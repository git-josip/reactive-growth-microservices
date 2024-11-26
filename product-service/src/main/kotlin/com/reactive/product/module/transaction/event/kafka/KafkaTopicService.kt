package com.reactive.product.module.transaction.event.kafka

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.stereotype.Service

@Service
class KafkaTopicService(private val adminClient: AdminClient) {
    @PostConstruct
    fun createTopicIfNotExists() {
        val topicName = "transactions"
        val topics = adminClient.listTopics().names().get()
        if (!topics.contains(topicName)) {
            val newTopic = NewTopic(topicName, 5, 1.toShort())
            adminClient.createTopics(listOf(newTopic)).all().get()
            println("Topic '$topicName' created")
        } else {
            println("Topic '$topicName' already exists")
        }
    }
}