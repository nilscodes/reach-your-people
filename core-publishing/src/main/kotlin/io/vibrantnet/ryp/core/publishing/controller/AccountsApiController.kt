package io.vibrantnet.ryp.core.publishing.controller

import io.ryp.shared.model.BasicAnnouncementWithIdDto
import io.vibrantnet.ryp.core.publishing.service.AccountsApiService
import org.springframework.beans.factory.annotation.Autowired
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
class AccountsApiController(@Autowired val service: AccountsApiService) {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/accounts/{accountId}/externalaccounts/{externalAccountId}/test"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun sendTestAnnouncement(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("externalAccountId") externalAccountId: Long,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<BasicAnnouncementWithIdDto>> {
        return service.sendTestAnnouncement(accountId, externalAccountId)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let {
                    UriComponentsBuilder.fromUriString(exchange.request.uri.scheme + "://" + exchange.request.uri.authority)
                        .path("/announcements/{id}")
                        .buildAndExpand(savedEntity.id)
                        .toUri()
                })
                    .body(savedEntity)
            }
    }


}
