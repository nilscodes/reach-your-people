package io.vibrantnet.ryp.core.verification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CoreVerificationConfiguration::class)
class CoreVerificationServiceForRypApplication

fun main(args: Array<String>) {
	runApplication<CoreVerificationServiceForRypApplication>(*args)
}
