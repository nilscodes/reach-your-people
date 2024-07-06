package io.vibrantnet.ryp.vibrant

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
@EnableWebFluxSecurity
class TestSecurityConfiguration {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // Disable CSRF and allow any exchanges
        http.csrf {
            it.disable()
        }
            .authorizeExchange {
                it.anyExchange().permitAll()
            }
        return http.build()
    }
}