package com.reactive.inventory.module.inventory.event.kafka

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.stereotype.Service

@Service
class KafkaTopicService(private val adminClient: AdminClient) {
    @PostConstruct
    fun createTopicIfNotExists() {
        val topicName = "inventory_validation"
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