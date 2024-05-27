package io.vibrantnet.ryp.core.subscription.service

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.ProjectDto
import io.ryp.shared.model.ProjectRole
import io.vibrantnet.ryp.core.subscription.persistence.Policy
import io.vibrantnet.ryp.core.subscription.persistence.Project
import io.vibrantnet.ryp.core.subscription.persistence.ProjectRepository
import io.vibrantnet.ryp.core.subscription.persistence.ProjectRoleAssignment
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
                roles = mutableSetOf(ProjectRoleAssignment(ProjectRole.OWNER, projectOwner)),
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

    override fun getProjectsForAccount(accountId: Long): Flux<ProjectDto> {
        return accountsApiService.getAccountById(accountId)
            .flatMapMany { account ->
                Flux.fromIterable(projectRepository.findDistinctByRolesAccountId(account.id!!).map { it.toDto() })
            }
            .onErrorResume {
                Flux.error(NoSuchElementException("No account with ID $accountId found"))
            }
    }

}