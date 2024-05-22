package io.vibrantnet.ryp.core.publishing.configuration

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
    fun discordQueue() = Queue("discord")

    @Bean
    fun smsQueue() = Queue("sms")

    @Bean
    fun pushApiQueue() = Queue("pushapi")

    @Bean
    fun messageConverter() = Jackson2JsonMessageConverter()
}