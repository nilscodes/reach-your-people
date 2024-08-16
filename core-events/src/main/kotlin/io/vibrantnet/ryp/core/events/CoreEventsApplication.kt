package io.vibrantnet.ryp.core.events

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(CoreEventsConfiguration::class)
class CoreEventsApplication

fun main(args: Array<String>) {
	runApplication<CoreEventsApplication>(*args)
}
