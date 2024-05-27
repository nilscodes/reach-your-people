package io.vibrantnet.ryp.vibrant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(IntegrationVibrantApplicationConfiguration::class)
class IntegrationVibrantApplication

fun main(args: Array<String>) {
	runApplication<IntegrationVibrantApplication>(*args)
}
