package io.vibrantnet.ryp.core.subscription.controller

import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.AccountPartialDto
import io.vibrantnet.ryp.core.subscription.service.AccountsApiService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class AccountsApiController(
    val service: AccountsApiService
) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/accounts"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @Valid @RequestBody accountDto: AccountDto,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<AccountDto>> {
        return service.createAccount(accountDto)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let { UriComponentsBuilder.fromUri(it).path("/{id}").buildAndExpand(savedEntity.id).toUri() })
                    .body(savedEntity)
            }
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAccountById(@PathVariable("accountId") accountId: Long) = service.getAccountById(accountId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{providerType}/{referenceId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun findAccountByProviderAndReferenceId(
        @PathVariable("providerType") providerType: String,
        @PathVariable("referenceId") referenceId: String) = service.findAccountByProviderAndReferenceId(providerType, referenceId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}/externalaccounts"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getLinkedExternalAccounts(@PathVariable("accountId") accountId: Long) = service.getLinkedExternalAccounts(accountId)

    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/accounts/{accountId}/externalaccounts/{externalAccountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun linkExternalAccount(
        @PathVariable("externalAccountId") externalAccountId: Long,
        @PathVariable("accountId") accountId: Long
    ) = service.linkExternalAccount(externalAccountId, accountId)

    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/accounts/{accountId}/externalaccounts/{externalAccountId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unlinkExternalAccount(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("externalAccountId") externalAccountId: Long
    ) = service.unlinkExternalAccount(accountId, externalAccountId)

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/accounts/{accountId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun updateAccountById(
        @PathVariable("accountId") accountId: Long,
        @Valid @RequestBody accountPartialDto: AccountPartialDto
    ) = service.updateAccountById(accountId, accountPartialDto)
}
