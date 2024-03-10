package io.vibrantnet.ryp.core.subscription.controller

import io.ryp.shared.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.service.ExternalAccountsApiService
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class ExternalAccountsApiController(
    val service: ExternalAccountsApiService
) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/externalaccounts"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun createExternalAccount(@Valid @RequestBody externalAccountDto: ExternalAccountDto, exchange: ServerWebExchange): Mono<ResponseEntity<ExternalAccountDto>> {
        return service.createExternalAccount(externalAccountDto)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let { UriComponentsBuilder.fromUri(it).path("/{id}").buildAndExpand(savedEntity.id).toUri() })
                    .body(savedEntity)
            }

    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/externalaccounts/{providerType}/{referenceId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun findExternalAccountByProviderAndReferenceId(
        @PathVariable("providerType") providerType: String,
        @PathVariable("referenceId") referenceId: String
    ) = service.findExternalAccountByProviderAndReferenceId(providerType, referenceId)
}
