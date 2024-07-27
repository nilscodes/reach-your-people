package io.vibrantnet.ryp.core.points.configuration

import io.vibrantnet.ryp.core.points.CorePointsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@Configuration
class SecurityConfig(
    private val config: CorePointsConfiguration,
) {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/actuator/health").permitAll()
                if (!config.security.apiKey.isNullOrBlank()) {
                    it.anyExchange().authenticated()
                } else {
                    it.anyExchange().permitAll()
                }
            }
        if (!config.security.apiKey.isNullOrBlank()) {
            http.addFilterAt(tokenAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        }
        return http.build()
    }

    @Bean
    fun tokenAuthenticationFilter() = TokenAuthenticationFilter(config.security.apiKey)
}

class TokenAuthenticationFilter(
    private val authToken: String?
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val token = exchange.request.headers.getFirst("Authorization")
        if (authToken == token) {
            val authentication = PreAuthenticatedAuthenticationToken(token, null, listOf(SimpleGrantedAuthority("INTERNAL_API")))
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        }
        return chain.filter(exchange)
    }
}