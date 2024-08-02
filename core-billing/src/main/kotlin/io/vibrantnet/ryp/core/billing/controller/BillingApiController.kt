package io.vibrantnet.ryp.core.billing.controller

import io.ryp.core.billing.model.BillDto
import io.vibrantnet.ryp.core.billing.service.BillingApiService
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
class BillingApiController(private val service: BillingApiService) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/billing/accounts/{accountId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createBill(
        @PathVariable("accountId") accountId: Long,
        @Valid @RequestBody(required = false) bill: BillDto,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<BillDto>> {
        return service.createBill(accountId, bill)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let {
                    UriComponentsBuilder.fromUri(it).path("/{id}").buildAndExpand(savedEntity.id).toUri()
                })
                    .body(savedEntity)
            }
    }


    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/billing/accounts/{accountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getBillsForAccount( @PathVariable("accountId") accountId: Long)
     = service.getBillsForAccount(accountId)
}
