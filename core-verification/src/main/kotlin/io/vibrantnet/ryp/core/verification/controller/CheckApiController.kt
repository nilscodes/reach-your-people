package io.vibrantnet.ryp.core.verification.controller

import io.vibrantnet.ryp.core.verification.service.CheckApiService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class CheckApiController(
    val service: CheckApiService
) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/cip66/{policyId}/{serviceName}/{referenceId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun verify(
        @PathVariable("policyId") policyId: String,
        @PathVariable("serviceName") serviceName: String,
        @PathVariable("referenceId") referenceId: String,
    ): Mono<Boolean> {
        return service.verify(policyId, serviceName, referenceId)
    }
}