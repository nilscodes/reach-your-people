package io.vibrantnet.ryp.core.subscription

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CoreSubscriptionApplication

fun main(args: Array<String>) {
	runApplication<CoreSubscriptionApplication>(*args)
}
