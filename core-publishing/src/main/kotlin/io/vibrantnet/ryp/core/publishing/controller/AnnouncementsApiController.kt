package io.vibrantnet.ryp.core.publishing.controller

import io.vibrantnet.ryp.core.publishing.service.AnnouncementsApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class AnnouncementsApiController(@Autowired val service: AnnouncementsApiService) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/announcements/{announcementId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAnnouncementById(@PathVariable("announcementId") announcementId: UUID) = service.getAnnouncementById(announcementId)

}
