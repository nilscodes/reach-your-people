package io.vibrantnet.ryp.core.subscription.service

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.mockk.*
import io.ryp.shared.model.PolicyDto
import io.ryp.shared.model.ProjectCategory
import io.ryp.shared.model.ProjectDto
import io.ryp.shared.model.ProjectPartialDto
import io.vibrantnet.ryp.core.subscription.persistence.Policy
import io.vibrantnet.ryp.core.subscription.persistence.Project
import io.vibrantnet.ryp.core.subscription.persistence.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.Optional

internal class ProjectsApiServiceVibrantTest {
    private val projectRepository = mockk<ProjectRepository>()
    private val accountsApiService = mockk<AccountsApiService>()
    private val projectsApiService = ProjectsApiServiceVibrant(projectRepository, accountsApiService)
    private val registrationTime = OffsetDateTime.now() // A reusable timestamp that remains consistent

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `adding a new project works if the owner exists`() {
        every { accountsApiService.getAccountById(69) } answers { Mono.just(makeAccountDto(69)) }
        every { projectRepository.save(any()) } returns makeProject(69, registrationTime)
        val result = projectsApiService.addNewProject(69, makeProjectDto(null))

        StepVerifier.create(result)
            .expectNext(makeProjectDto(69).copy(
                tags = setOf("test", "project"),
                registrationTime = registrationTime
            ))
            .verifyComplete()

    }

    @Test
    fun `adding a new project does nothing if the owner does not exist`() {
        every { accountsApiService.getAccountById(69) } answers { Mono.empty() }
        val result = projectsApiService.addNewProject(69, makeProjectDto(null))

        StepVerifier.create(result)
            .verifyComplete()

        verify { projectRepository wasNot Called }
    }

    @Test
    fun `listing projects works`() {
        every { projectRepository.findAll() } returns listOf(
            makeProject(69, registrationTime),
            makeProject(70, registrationTime),
            makeProject(71, registrationTime),
        )
        val result = projectsApiService.listProjects()

        StepVerifier.create(result)
            .expectNext(makeProjectDto(69).copy(registrationTime = registrationTime))
            .expectNext(makeProjectDto(70).copy(registrationTime = registrationTime))
            .expectNext(makeProjectDto(71).copy(registrationTime = registrationTime))
            .verifyComplete()
    }

    @Test
    fun `getting a single project that exists works`() {
        every { projectRepository.findById(69) } returns Optional.of(makeProject(69, registrationTime))
        val result = projectsApiService.getProject(69)

        StepVerifier.create(result)
            .expectNext(makeProjectDto(69).copy(registrationTime = registrationTime))
            .verifyComplete()
    }

    @Test
    fun `getting a single project that does not exist gives the right error`() {
        every { projectRepository.findById(69) } returns Optional.empty()
        val result = projectsApiService.getProject(69)

        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `updating a project works`() {
        val verifiedDate = OffsetDateTime.now()
        every { projectRepository.findById(69) } returns Optional.of(makeProject(69, registrationTime))
        every { projectRepository.save(any()) } answers { firstArg() }

        val result = projectsApiService.updateProject(69, ProjectPartialDto(
            name = "Updated Project",
            logo = "https://example.com/updated.png",
            url = "https://example.com/updated",
            description = "An updated project",
            category = ProjectCategory.nFT,
            tags = setOf("updated", "project"),
            policies = setOf(
                PolicyDto("Updated Policy", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", null),
                PolicyDto("Updated Policy 2", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", null),
            ),
            manuallyVerified = verifiedDate,
        ))

        StepVerifier.create(result)
            .expectNext(makeProjectDto(69).copy(
                name = "Updated Project",
                logo = "https://example.com/updated.png",
                url = "https://example.com/updated",
                description = "An updated project",
                category = ProjectCategory.nFT,
                tags = setOf("updated", "project"),
                policies = setOf(
                    PolicyDto("Updated Policy", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", null),
                    PolicyDto("Updated Policy 2", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", null),
                ),
                manuallyVerified = verifiedDate,
                registrationTime = registrationTime,
            ))
            .verifyComplete()
    }

    @Test
    fun `updating a project that does not exist errors out appropriately`() {
        every { projectRepository.findById(69) } returns Optional.empty()
        val result = projectsApiService.updateProject(69, ProjectPartialDto(
            name = "Updated Project",
        ))

        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `getting all projects that an account is associated with via roles works`() {
        every { accountsApiService.getAccountById(69) } answers { Mono.just(makeAccountDto(69)) }
        every { projectRepository.findDistinctByRolesAccountId(69) } returns listOf(
            makeProject(69, registrationTime),
            makeProject(70, registrationTime),
            makeProject(71, registrationTime),
        )
        val result = projectsApiService.getProjectsForAccount(69)

        StepVerifier.create(result)
            .expectNext(makeProjectDto(69).copy(registrationTime = registrationTime))
            .expectNext(makeProjectDto(70).copy(registrationTime = registrationTime))
            .expectNext(makeProjectDto(71).copy(registrationTime = registrationTime))
            .verifyComplete()
    }

    @Test
    fun `getting all projects for an account that does not exist errors out properly`() {
        every { accountsApiService.getAccountById(69) } answers { Mono.empty() }
        val result = projectsApiService.getProjectsForAccount(69)

        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    private fun makeProjectDto(projectId: Long?) = ProjectDto(
        id = projectId,
        name = "Test Project",
        logo = "https://example.com/logo.png",
        url = "https://example.com",
        description = "A test project",
        category = ProjectCategory.nFT,
        tags = setOf("test", "project"),
        policies = setOf(
            PolicyDto("Test Policy", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", null),
            PolicyDto("Test Policy 2", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", null),
        )
    )

}

fun makeProject(projectId: Long, registrationTime: OffsetDateTime = OffsetDateTime.now()) = Project(
    id = projectId,
    name = "Test Project",
    logo = "https://example.com/logo.png",
    url = "https://example.com",
    description = "A test project",
    category = ProjectCategory.nFT,
    tags = mutableSetOf("test", "project"),
    policies = mutableSetOf(
        Policy("Test Policy", PolicyId("df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058"), null),
        Policy("Test Policy 2", PolicyId("4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f"), null),
    ),
    registrationTime = registrationTime,
)