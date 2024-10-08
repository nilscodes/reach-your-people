package io.vibrantnet.ryp.core.subscription.service

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.ProjectDto
import io.ryp.shared.model.ProjectPartialDto
import io.ryp.shared.model.ProjectRole
import io.vibrantnet.ryp.core.subscription.persistence.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProjectsApiServiceVibrant(
    val projectRepository: ProjectRepository,
    val accountsApiService: AccountsApiService
): ProjectsApiService {
    override fun addNewProject(projectOwner: Long, project: ProjectDto): Mono<ProjectDto> {
        return accountsApiService.getAccountById(projectOwner)
            .map { val newProject = Project(
                name = project.name,
                logo = project.logo,
                url = project.url,
                description = project.description,
                category = project.category,
                tags = project.tags.toMutableSet(),
                policies = project.policies.map { Policy(it.name, PolicyId(it.policyId)) }.toMutableSet(),
                stakepools = project.stakepools.map { Stakepool(it.poolHash, it.verificationNonce) }.toMutableSet(),
                roles = mutableSetOf(ProjectRoleAssignment(ProjectRole.OWNER, projectOwner)),
                manuallyVerified = project.manuallyVerified,
            )
            projectRepository.save(newProject).toDto()
        }
    }

    override fun listProjects(): Flux<ProjectDto> {
        return Flux.fromIterable(projectRepository.findAll().map { it.toDto() })
    }

    override fun getProject(projectId: Long): Mono<ProjectDto> {
        val project = projectRepository.findById(projectId)
        if (project.isPresent) {
            return Mono.just(project.get().toDto())
        }
        return Mono.error(NoSuchElementException("No project with ID $projectId found"))
    }

    override fun updateProject(projectId: Long, projectPartial: ProjectPartialDto): Mono<ProjectDto> {
        val project = projectRepository.findById(projectId)
        if (project.isPresent) {
            val updatedProject = project.get().apply {
                projectPartial.name?.let { name = it }
                projectPartial.logo?.let { logo = it }
                projectPartial.url?.let { url = it }
                projectPartial.description?.let { description = it }
                projectPartial.category?.let { category = it }
                projectPartial.tags?.let { tags = it.toMutableSet() }
                projectPartial.policies?.let { policies = it.map { Policy(it.name, PolicyId(it.policyId)) }.toMutableSet() }
                projectPartial.stakepools?.let { stakepools = it.map { Stakepool(it.poolHash, it.verificationNonce) }.toMutableSet() }
                projectPartial.manuallyVerified?.let { manuallyVerified = it }
            }
            return Mono.just(projectRepository.save(updatedProject).toDto())
        }
        return Mono.error(NoSuchElementException("No project with ID $projectId found"))
    }

    override fun getProjectsForAccount(accountId: Long): Flux<ProjectDto> {
        return accountsApiService.getAccountById(accountId)
            .switchIfEmpty(Mono.error(NoSuchElementException("No account with ID $accountId found")))
            .flatMapMany { account ->
                Flux.fromIterable(projectRepository.findDistinctByRolesAccountId(account.id!!).map { it.toDto() })
            }
    }

}