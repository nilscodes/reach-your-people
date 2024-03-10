package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.ProjectDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProjectsApiService {

    /**
     * POST /projects : Add new project
     *
     * @param project  (optional)
     * @return Created (status code 201)
     * @see ProjectsApi#addNewProject
     */
    fun addNewProject(projectOwner: Long, project: ProjectDto): Mono<ProjectDto>

    /**
     * GET /projects : List all projects
     *
     * @return List of all available projects (status code 200)
     * @see ProjectsApi#listProjects
     */
    fun listProjects(): Flux<ProjectDto>

    /**
     * GET /accounts/{accountId}/projects : Get projects owned by this account
     * Get all projects that this account is an owner of.
     *
     * @param accountId The numeric ID of an account (required)
     * @return All owned projects (status code 200)
     * @see ProjectsApi#getProjectsForAccount
     */
    fun getProjectsForAccount(accountId: Long): Flux<ProjectDto>

    /**
     * GET /projects/{projectId} : Get a specific project by project ID
     *
     * @param projectId The numeric ID of a Project (required)
     * @return OK (status code 200)
     * @see ProjectsApi#getProject
     */
    fun getProject(projectId: Long): Mono<ProjectDto>

    /**
     * GET /projects/{projectId}/subscriptions : Get all subscription for a project
     *
     * @param projectId The numeric ID of a Project (required)
     * @return
     * @see ProjectsApi#getAllSubscriptionsForProject
     */
    //fun getAllSubscriptionsForProject(projectId: kotlin.Long): Unit
}
