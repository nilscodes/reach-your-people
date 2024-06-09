package io.vibrantnet.ryp.core.redirect

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableConfigurationProperties(CoreRedirectConfiguration::class)
class CoreRedirectApplication

fun main(args: Array<String>) {
	runApplication<CoreRedirectApplication>(*args)
}
