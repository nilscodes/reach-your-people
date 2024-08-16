package io.vibrantnet.ryp.core.verification.controller

import io.vibrantnet.ryp.core.verification.service.DrepsApiService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class DrepsApiController(
    val service: DrepsApiService,
) {


    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/dreps/{drepId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getDRepDetails(@PathVariable("drepId") drepId: String) = service.getDRepDetails(drepId)

}
