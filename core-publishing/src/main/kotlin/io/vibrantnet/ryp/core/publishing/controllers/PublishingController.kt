package io.vibrantnet.ryp.core.publishing.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class PublishingController {
    @GetMapping("/")
    fun index(): Mono<String> {
        return Mono.just("Hello, world!")
    }
}
