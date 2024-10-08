package io.vibrantnet.ryp.core.points

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CorePointsConfiguration::class)
class CorePointsApplication

fun main(args: Array<String>) {
	runApplication<CorePointsApplication>(*args)
}
