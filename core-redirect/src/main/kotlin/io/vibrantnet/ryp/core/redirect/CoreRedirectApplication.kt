package io.vibrantnet.ryp.core.redirect

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CoreRedirectConfiguration::class)
class CoreRedirectApplication

fun main(args: Array<String>) {
	runApplication<CoreRedirectApplication>(*args)
}
