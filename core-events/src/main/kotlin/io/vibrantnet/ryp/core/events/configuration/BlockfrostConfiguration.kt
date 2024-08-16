package io.vibrantnet.ryp.core.events.configuration

import io.vibrantnet.ryp.core.events.CoreEventsConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class BlockfrostConfiguration(
    private val configuration: CoreEventsConfiguration,
) {
    @Bean
    fun blockfrostClient(): WebClient = WebClient.builder()
        .baseUrl(configuration.blockfrost?.url ?: "")
        // Include a header with the API key in every request
        .defaultHeader("project_id", configuration.blockfrost?.apiKey ?: "")
        .build()
}