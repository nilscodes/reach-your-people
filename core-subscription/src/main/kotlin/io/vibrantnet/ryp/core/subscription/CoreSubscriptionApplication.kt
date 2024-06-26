package io.vibrantnet.ryp.core.subscription

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(CoreSubscriptionConfiguration::class)
@EnableAspectJAutoProxy
class CoreSubscriptionApplication

fun main(args: Array<String>) {
	runApplication<CoreSubscriptionApplication>(*args)
}
