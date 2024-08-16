package io.vibrantnet.ryp.core.events.configuration

import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun eventNotificationsQueue() = Queue("event-notifications")

    @Bean
    fun messageConverter() = Jackson2JsonMessageConverter()
}