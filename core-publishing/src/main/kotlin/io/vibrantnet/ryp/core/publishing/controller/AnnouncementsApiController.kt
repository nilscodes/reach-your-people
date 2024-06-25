package io.vibrantnet.ryp.core.publishing.controller

import io.ryp.shared.model.BasicAnnouncementDto
import io.vibrantnet.ryp.core.publishing.model.AnnouncementDto
import io.vibrantnet.ryp.core.publishing.service.AnnouncementsApiService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.util.*

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class AnnouncementsApiController(@Autowired val service: AnnouncementsApiService) {



    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["projects/{projectId}/announcements"],
        consumes = ["application/json"]
    )
    fun publishAnnouncementForProject(
        @PathVariable("projectId") projectId: Long,
        @Valid @RequestBody announcement: BasicAnnouncementDto,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<AnnouncementDto>> {
        return service.publishAnnouncementForProject(projectId, announcement)
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

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/projects/{projectId}/announcements"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun listAnnouncementsForProject(@PathVariable("projectId") projectId: Long) = service.listAnnouncementsForProject(projectId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/projects/{projectId}/roles/{accountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getPublishingPermissionsForAccount(
        @PathVariable("projectId") projectId: Long,
        @PathVariable("accountId") accountId: Long
    )
        = service.getPublishingPermissionsForAccount(projectId, accountId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/announcements/{announcementId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAnnouncementById(@PathVariable("announcementId") announcementId: UUID) = service.getAnnouncementById(announcementId)


}
