package io.vibrantnet.ryp.core.publishing.controller

import io.ryp.shared.model.BasicAnnouncementDto
import io.vibrantnet.ryp.core.publishing.service.AnnouncementsApiService
import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated
import org.springframework.beans.factory.annotation.Autowired

import jakarta.validation.Valid
import org.springframework.http.HttpStatus

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
class AnnouncementsApiController(@Autowired val service: AnnouncementsApiService) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/announcements/{projectId}"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun publishAnnouncementForProject(
        @PathVariable("projectId") projectId: Long,
        @Valid @RequestBody announcement: BasicAnnouncementDto,
    ) = service.publishAnnouncementForProject(projectId, announcement)


}
