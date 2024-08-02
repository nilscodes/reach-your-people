package io.vibrantnet.ryp.core.billing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(CoreBillingConfiguration::class)
@EnableScheduling
class CoreBillingApplication

fun main(args: Array<String>) {
	runApplication<CoreBillingApplication>(*args)
}
