package io.vibrantnet.ryp.core.points.configuration

import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun claimsQueue() = Queue("pointclaims")

    @Bean
    fun claimsFulfilledQueue() = Queue("pointclaimsfulfilled")

    @Bean
    fun messageConverter() = Jackson2JsonMessageConverter()
}