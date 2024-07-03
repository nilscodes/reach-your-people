package io.vibrantnet.ryp.core.publishing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(CorePublishingConfiguration::class)
@EnableScheduling
class CorePublishingApplication

fun main(args: Array<String>) {
	runApplication<CorePublishingApplication>(*args)
}
