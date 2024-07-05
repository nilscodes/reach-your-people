package io.vibrantnet.ryp.core.publishing.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.*
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import io.vibrantnet.ryp.core.publishing.model.AnnouncementStatus
import io.vibrantnet.ryp.core.publishing.model.Note
import io.vibrantnet.ryp.core.publishing.model.UserNotAuthorizedToPublishException
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsRepository
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsUpdateService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.TimeUnit

internal class AnnouncementsApiServiceVibrantTest {

    private val subscriptionService = mockk<SubscriptionService>()
    private val verifyService = mockk<VerifyService>()
    private val rabbitTemplate = mockk<RabbitTemplate>()
    private val redisTemplate = mockk<RedisTemplate<String, Any>>()
    private val objectMapper = jacksonObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
    private val announcementsRepository = mockk<AnnouncementsRepository>()
    private val announcementUpdateService = mockk<AnnouncementsUpdateService>()
    private val redirectService = mockk<RedirectService>()
    private val config = CorePublishingConfiguration("", "", "", "https://ryp.io")
    private val announcementsApiService = AnnouncementsApiServiceVibrant(
        subscriptionService,
        verifyService,
        rabbitTemplate,
        redisTemplate,
        objectMapper,
        announcementsRepository,
        announcementUpdateService,
        redirectService,
        config
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { subscriptionService.getLinkedExternalAccounts(12) } answers {
            Flux.fromIterable(listOf(
                makeLinkedExternalAccountDto(12, 1212),
                makeLinkedExternalAccountDto(134, 5124, ExternalAccountRole.ADMIN),
            ))
        }
        every { redirectService.createShortUrlWithFallback(any()) } answers { Mono.just("https://go.ryp.io/test") }
        every { announcementsRepository.save(any()) } answers { Mono.just(firstArg()) }
        every { announcementUpdateService.updateAnnouncementStatus(any(), any()) } answers { Mono.empty() }
        every { announcementUpdateService.updateAnnouncementStatistics(any(), any(), any()) } answers { Mono.empty() }
    }

    @Test
    fun `publishing to announcement queue works if policy is manually verified`() {
        val valueOperations = mockk<ValueOperations<String, Any>>(relaxed = true)
        every { redisTemplate.opsForValue() } answers { valueOperations }
        every { rabbitTemplate.convertAndSend("announcements", match<AnnouncementJobDto> { it.projectId == 82L }) } just Runs
        every { subscriptionService.getProject(82) } answers { Mono.just(makeProjectDto(82)) }
        val result = announcementsApiService.publishAnnouncementForProject(82, makeTestAnnouncement(
            listOf("test-policy")
        ))

        var announcementId = UUID.fromString("00000000-0000-0000-0000-000000000000")
        StepVerifier.create(result)
            .assertNext {
                announcementId = it.id
                assertEquals(82, it.projectId)
                assertEquals("Test Announcement", (it.announcement.`object` as Note).summary)
                assertEquals("This is a test announcement", (it.announcement.`object` as Note).content)
                assertEquals("https://go.ryp.io/test", it.shortLink)
            }
            .verifyComplete()

        verify(exactly = 1) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 1) { valueOperations.set(match {
            it == "announcementsdata:$announcementId"
        }, match<BasicAnnouncementWithIdDto> {
            it.id == announcementId && it.title == "Test Announcement" && it.content == "This is a test announcement"
        }, 48, TimeUnit.HOURS) }
    }

    @Test
    fun `publishing to announcement queue works for a CIP-0066 verified policy`() {
        val valueOperations = mockk<ValueOperations<String, Any>>(relaxed = true)
        every { redisTemplate.opsForValue() } answers { valueOperations }
        every { rabbitTemplate.convertAndSend("announcements", match<AnnouncementJobDto> { it.projectId == 82L }) } just Runs
        every { subscriptionService.getProject(82) } answers { Mono.just(makeProjectDto(82, null)) }
        every { verifyService.verifyCip66("test-policy", "discord", "5124") } answers { Mono.just(false) }
        every { verifyService.verifyCip66("test-policy", "discord", "1212") } answers { Mono.just(true) }
        val result = announcementsApiService.publishAnnouncementForProject(82, makeTestAnnouncement(
            listOf("test-policy")
        ))

        var announcementId = UUID.fromString("00000000-0000-0000-0000-000000000000")
        StepVerifier.create(result)
            .assertNext {
                announcementId = it.id
                assertEquals(82, it.projectId)
                assertEquals("Test Announcement", (it.announcement.`object` as Note).summary)
                assertEquals("This is a test announcement", (it.announcement.`object` as Note).content)
                assertEquals("https://go.ryp.io/test", it.shortLink)
            }
            .verifyComplete()

        verify(exactly = 1) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 1) { valueOperations.set(match {
            it == "announcementsdata:$announcementId"
        }, match<BasicAnnouncementWithIdDto> {
            it.id == announcementId && it.title == "Test Announcement" && it.content == "This is a test announcement"
        }, 48, TimeUnit.HOURS) }
    }

    @Test
    fun `publishing to announcement queue fails if publishing to multiple policies and at least one is not verified`() {
        val valueOperations = mockk<ValueOperations<String, Any>>(relaxed = true)
        every { redisTemplate.opsForValue() } answers { valueOperations }
        every { rabbitTemplate.convertAndSend("announcements", match<AnnouncementJobDto> { it.projectId == 82L }) } just Runs
        every { subscriptionService.getProject(82) } answers { Mono.just(makeProjectDto(82)) }
        every { verifyService.verifyCip66("test-policy-2", "discord", "5124") } answers { Mono.just(false) }
        every { verifyService.verifyCip66("test-policy-2", "discord", "1212") } answers { Mono.just(false) }
        val result = announcementsApiService.publishAnnouncementForProject(82, makeTestAnnouncement(
            listOf("test-policy", "test-policy-2")
        ))

        StepVerifier.create(result)
            .expectError(UserNotAuthorizedToPublishException::class.java)
            .verify()

        verify(exactly = 0) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 0) { valueOperations.set(any(), any(), any(), any()) }
    }

    @Test
    fun `publishing to an empty policy list fails`() {
        every { subscriptionService.getProject(82) } answers { Mono.just(makeProjectDto(82)) }
        val result = announcementsApiService.publishAnnouncementForProject(82, makeTestAnnouncement(emptyList()))

        StepVerifier.create(result)
            .expectError(UserNotAuthorizedToPublishException::class.java)
            .verify()

        verify(exactly = 0) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
    }

    @Test
    fun `listing announcements for a project works`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        every { announcementsRepository.findByProjectId(82) } answers {
            Flux.fromIterable(listOf(
                announcementFromBasicAnnouncement(makeTestAnnouncement(
                    listOf("test-policy")
                ).toBasicAnnouncementWithIdDto(uuid1, "https://ryp.io/announcements/1"), 82, config),
                announcementFromBasicAnnouncement(makeTestAnnouncement(
                    listOf("test-policy")
                ).toBasicAnnouncementWithIdDto(uuid2, "https://ryp.io/announcements/2"), 82, config),
            ))
        }
        val result = announcementsApiService.listAnnouncementsForProject(82)

        StepVerifier.create(result)
            .assertNext {
                assertEquals(uuid1, it.id)
                assertEquals("https://ryp.io/announcements/1", it.shortLink)
                assertEquals("Test Announcement", (it.announcement.`object` as Note).summary)
                assertEquals("This is a test announcement", (it.announcement.`object` as Note).content)
            }
            .assertNext {
                assertEquals(uuid2, it.id)
                assertEquals("https://ryp.io/announcements/2", it.shortLink)
                assertEquals("Test Announcement", (it.announcement.`object` as Note).summary)
                assertEquals("This is a test announcement", (it.announcement.`object` as Note).content)
            }
            .verifyComplete()
    }

    @Test
    fun `get individual announcement by ID works`() {
        val uuid = UUID.randomUUID()
        every { announcementsRepository.findById(uuid.toString()) } answers {
            Mono.just(announcementFromBasicAnnouncement(makeTestAnnouncement(
                listOf("test-policy")
            ).toBasicAnnouncementWithIdDto(uuid, "https://ryp.io/announcements/1"), 82, config))
        }
        val result = announcementsApiService.getAnnouncementById(uuid)

        StepVerifier.create(result)
            .assertNext {
                assertEquals(uuid, it.id)
                assertEquals("https://ryp.io/announcements/1", it.shortLink)
                assertEquals("Test Announcement", (it.announcement.`object` as Note).summary)
                assertEquals("This is a test announcement", (it.announcement.`object` as Note).content)
            }
            .verifyComplete()
    }

    @Test
    fun `sending message to subscribers works`() {
        val uuid = UUID.randomUUID()
        val testAnnouncement = makeTestAnnouncement(listOf("test-policy")).toBasicAnnouncementWithIdDto(uuid, "https://ryp.io/announcements/1")
        val valueOperations = mockk<ValueOperations<String, Any>>(relaxed = true)
        val listOperations = mockk<ListOperations<String, Any>>(relaxed = true)
        every { redisTemplate.opsForValue() } answers { valueOperations }
        every { valueOperations["announcementsdata:$uuid"] } returns testAnnouncement
        every { redisTemplate.opsForList() } answers { listOperations }
        every { redisTemplate.delete(any<String>()) } returns true
        every { listOperations.range("announcements:$uuid", 0, -1) } answers {
            listOf(
                AnnouncementRecipientDto(12, "discord", 25, "123", null, SubscriptionStatus.SUBSCRIBED),
                AnnouncementRecipientDto(13, "sms", 26, "456", null, SubscriptionStatus.SUBSCRIBED),
            )
        }

        every { subscriptionService.getProject(82) } answers { Mono.just(makeProjectDto(82)) }
        every { rabbitTemplate.convertAndSend(any(), any<MessageDto>()) } just Runs
        announcementsApiService.sendAnnouncementToSubscribers(AnnouncementJobDto(82, uuid))

        verify(exactly = 1) { rabbitTemplate.convertAndSend("discord", MessageDto(
            "123",
            testAnnouncement,
            null,
            BasicProjectDto(makeProjectDto(82)),
        )) }
        verify(exactly = 1) { rabbitTemplate.convertAndSend("sms", MessageDto(
            "456",
            testAnnouncement,
            null,
            BasicProjectDto(makeProjectDto(82)),
        )) }
        // Redis key should be deleted after sending messages
        verify(exactly = 1) { redisTemplate.delete("announcements:$uuid") }
        verify(exactly = 1) { redisTemplate.delete("announcementsdata:$uuid") }
        // Announcement status should be updated to SENT
        verify(exactly = 1) { announcementUpdateService.updateAnnouncementStatistics(uuid.toString(), match {
            it.sent == mapOf("discord" to 1L, "sms" to 1L)
        }, AnnouncementStatus.PUBLISHED) }
    }

    @Test
    fun `sending a message is marked correctly as failed if redis retrieval fails`() {
        val uuid = UUID.randomUUID()
        val valueOperations = mockk<ValueOperations<String, Any>>(relaxed = true)
        val listOperations = mockk<ListOperations<String, Any>>(relaxed = true)
        every { redisTemplate.opsForValue() } answers { valueOperations }
        every { valueOperations["announcementsdata:$uuid"] } returns null
        every { redisTemplate.opsForList() } answers { listOperations }
        every { listOperations.range("announcements:$uuid", 0, -1) } answers {
            listOf(
                AnnouncementRecipientDto(12, "discord", 25, "123", null, SubscriptionStatus.SUBSCRIBED),
            )
        }

        every { subscriptionService.getProject(82) } answers { Mono.just(makeProjectDto(82)) }
        announcementsApiService.sendAnnouncementToSubscribers(AnnouncementJobDto(82, uuid))

        // Announcement status should be updated to FAILED
        verify(exactly = 1) { announcementUpdateService.updateAnnouncementStatus(uuid.toString(), AnnouncementStatus.FAILED) }
    }

    private fun makeTestAnnouncement(policies: List<String>) = BasicAnnouncementDto(
        title = "Test Announcement",
        content = "This is a test announcement",
        author = 12,
        policies = policies,
    )

    private fun makeProjectDto(projectId: Long, manuallyVerified: OffsetDateTime? = OffsetDateTime.now()) = ProjectDto(
        id = projectId,
        name = "Test Project",
        description = "This is a test project",
        logo = "",
        url = "https://ryp.io/projects/$projectId",
        category = ProjectCategory.nFT,
        policies = setOf(
            PolicyDto("Test Policy", "test-policy", manuallyVerified),
            PolicyDto("Test Policy 2", "test-policy-2", null),
        )
    )
}