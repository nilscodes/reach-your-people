package io.vibrantnet.ryp.core.verification.controller

import io.vibrantnet.ryp.core.verification.service.TransactionsApiService
import org.springframework.http.HttpStatus

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated
import org.springframework.beans.factory.annotation.Autowired

import jakarta.validation.constraints.Pattern

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class TransactionsApiController(@Autowired(required = true) val service: TransactionsApiService) {


    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/transactions/{transactionHash}/summary"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getTransactionSummary(@Pattern(regexp = "^[a-zA-Z0-9]{64}$") @PathVariable("transactionHash") transactionHash: String) =
        service.getTransactionSummary(transactionHash)
}
