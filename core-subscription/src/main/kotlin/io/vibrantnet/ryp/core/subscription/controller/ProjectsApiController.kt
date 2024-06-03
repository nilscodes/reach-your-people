package io.vibrantnet.ryp.core.subscription.controller

import io.ryp.shared.model.ProjectDto
import io.ryp.shared.model.ProjectPartialDto
import io.vibrantnet.ryp.core.subscription.service.ProjectsApiService
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
class ProjectsApiController(val service: ProjectsApiService) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/projects"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun addNewProject(
        @Valid @RequestBody project: ProjectDto,
        @RequestParam("projectOwner") projectOwner: Long,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ProjectDto>> {
        return service.addNewProject(projectOwner, project)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let { UriComponentsBuilder.fromUri(it).path("/{id}").buildAndExpand(savedEntity.id).toUri() })
                    .body(savedEntity)
            }
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/projects"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun listProjects() = service.listProjects()

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/projects/{projectId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getProject(@PathVariable("projectId") projectId: Long) = service.getProject(projectId)

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/projects/{projectId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun updateProject(
        @PathVariable("projectId") projectId: Long,
        @Valid @RequestBody projectPartial: ProjectPartialDto,
    ) = service.updateProject(projectId, projectPartial)

//    @RequestMapping(
//        method = [RequestMethod.GET],
//        value = ["/projects/{projectId}/subscriptions"]
//    )
//    fun getAllSubscriptionsForProject( @PathVariable("projectId") projectId: kotlin.Long): ResponseEntity<Unit> {
//        return ResponseEntity(service.getAllSubscriptionsForProject(projectId), )
//    }
}
