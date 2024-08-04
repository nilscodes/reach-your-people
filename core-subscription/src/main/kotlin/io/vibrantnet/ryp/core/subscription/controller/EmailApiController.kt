package io.vibrantnet.ryp.core.subscription.controller

import io.vibrantnet.ryp.core.subscription.model.UnsubscribeFromEmailRequest
import io.vibrantnet.ryp.core.subscription.service.ExternalAccountsApiService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class EmailApiController(
    private val service: ExternalAccountsApiService,
) {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/email/unsubscribe"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unsubscribeFromEmail(@Valid @RequestBody unsubscribeFromEmailRequest: UnsubscribeFromEmailRequest) =
        service.unsubscribeFromEmail(unsubscribeFromEmailRequest)
}


