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
    fun emailQueue() = Queue("email")

    @Bean
    fun googleQueue() = Queue("google")

    @Bean
    fun telegramQueue() = Queue("telegram")

    @Bean
    fun eventNotificationsQueue() = Queue("event-notifications")

    @Bean
    fun discordStatisticsQueue() = Queue("statistics-discord")

    @Bean
    fun smsStatisticsQueue() = Queue("statistics-sms")

    @Bean
    fun pushApiStatisticsQueue() = Queue("statistics-pushapi")

    @Bean
    fun emailStatisticsQueue() = Queue("statistics-email")

    @Bean
    fun telegramStatisticsQueue() = Queue("statistics-telegram")

    @Bean
    fun messageConverter() = Jackson2JsonMessageConverter()
}