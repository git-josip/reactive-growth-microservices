package com.reactive.order.module.order.event.kafka

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.stereotype.Component

@Component
class KafkaTopicAutoCreation(private val adminClient: AdminClient) {
    @PostConstruct
    fun createTopicIfNotExists() {
        val topicName = "orders"
        val topics = adminClient.listTopics().names().get()
        if (!topics.contains(topicName)) {
            val newTopic = NewTopic(topicName, 5, 1.toShort())
            try {
                adminClient.createTopics(listOf(newTopic)).all().get()
                println("Topic '$topicName' created")
            } catch (e: Exception) {
                println("Error creating topic '$topicName'")
                e.printStackTrace()
            }
        } else {
            println("Topic '$topicName' already exists")
        }
    }
}