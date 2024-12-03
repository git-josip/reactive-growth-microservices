package com.reactive.product.common.configuration

import brave.Tracing
import brave.kafka.clients.KafkaTracing
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaTracingConfig(
    private val tracing: Tracing,
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun kafkaTracing(): KafkaTracing {
        return KafkaTracing.newBuilder(tracing).build()
    }

    @Bean
    fun consumerFactory(kafkaTracing: KafkaTracing): ConsumerFactory<String, String> {
        val configProps = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers.joinToString(separator = ","),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to "product-service",
        )
        val defaultKafkaConsumerFactory = DefaultKafkaConsumerFactory<String, String>(configProps)

        defaultKafkaConsumerFactory.addPostProcessor { consumer ->
            kafkaTracing.consumer(consumer)
        }

        return defaultKafkaConsumerFactory
    }

    @Bean
    fun producerFactory(kafkaTracing: KafkaTracing): ProducerFactory<String, String> {
        val configProps = mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers.joinToString(separator = ","),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        )
        val defaultKafkaProducerFactory = DefaultKafkaProducerFactory<String, String>(configProps)

        defaultKafkaProducerFactory.addPostProcessor { producer ->
            kafkaTracing.producer(producer)
        }

        return defaultKafkaProducerFactory
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, String>): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        return factory
    }
}