package io.vibrantnet.ryp.core.subscription.configuration

import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun announcementQueue() = Queue("announcements")

    @Bean
    fun completedQueue() = Queue("completed")

    @Bean
    fun snapshotQueue() = Queue("snapshot")

    @Bean
    fun snapshotCompletedQueue() = Queue("snapshotcompleted")

    @Bean
    fun messageConverter() = Jackson2JsonMessageConverter()
}