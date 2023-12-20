package io.vibrantnet.ryp.core.subscription.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class SubscriptionController {
    @GetMapping("/")
    fun index(): Mono<String> {
        return Mono.just("Hello, world!")
    }
}