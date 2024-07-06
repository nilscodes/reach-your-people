package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.subscription.controller.makeProjectDto
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountWithAccountProjection
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

internal class SubscriptionServiceVibrantTest {

    private val projectsService = mockk<ProjectsApiService>()
    private val externalAccountRepository = mockk<ExternalAccountRepository>()
    private val redisTemplate = mockk<RedisTemplate<String, Any>>()
    private val opsForList = mockk<ListOperations<String, Any>>()
    private val rabbitTemplate = mockk<RabbitTemplate>()
    private val objectMapper = jacksonObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
    private val subscriptionServiceVibrant = SubscriptionServiceVibrant(
        projectsService,
        externalAccountRepository,
        redisTemplate,
        rabbitTemplate,
        objectMapper,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { redisTemplate.opsForList() } returns opsForList
        every { opsForList.rightPushAll(any(), any()) } returns 2
        every { redisTemplate.expire(any(), any(), any()) } returns true
        every { rabbitTemplate.convertAndSend(any(), any<UUID>()) } returns Unit
    }

    @Test
    fun `preparing recipients should just retrieve explicit subscribers and send to complete queue if no policies are found`() {
        every { projectsService.getProject(420) } answers {
            Mono.just(makeProjectDto(420).copy(policies = emptySet()))
        }
        every { externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(420, SubscriptionStatus.SUBSCRIBED) } returns listOf(
            makeExternalAccountWithAccountProjection(40),
            makeExternalAccountWithAccountProjection(41),
        )
        val announcementId = UUID.randomUUID()
        subscriptionServiceVibrant.prepareRecipients(AnnouncementJobDto(420, announcementId))

        verify(exactly = 1) {
            opsForList.rightPushAll("announcements:$announcementId", any())
            redisTemplate.expire("announcements:$announcementId", 48, java.util.concurrent.TimeUnit.HOURS)
        }
        verify(exactly = 1) { rabbitTemplate.convertAndSend("completed", announcementId) }
    }

    @Test
    fun `preparing recipients should send snapshot request if policies are present in the announcement`() {
        every { projectsService.getProject(420) } answers {
            Mono.just(makeProjectDto(420))
        }
        every { externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(420, SubscriptionStatus.SUBSCRIBED) } returns listOf(
            makeExternalAccountWithAccountProjection(40),
            makeExternalAccountWithAccountProjection(41),
        )
        val announcementId = UUID.randomUUID()
        val announcement = AnnouncementJobDto(420, announcementId)
        subscriptionServiceVibrant.prepareRecipients(announcement)

        verify(exactly = 0) {
            opsForList.rightPushAll("announcements:$announcementId", any())
            redisTemplate.expire("announcements:$announcementId", 48, java.util.concurrent.TimeUnit.HOURS)
        } // No announcement published when we make a snapshot request
        verify(exactly = 1) { rabbitTemplate.convertAndSend("snapshot", SnapshotRequestDto(
            announcement,
            listOf("df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f")
        )) }
    }

    @Test
    fun `snapshot completion should lead to an announcement being sent with the right recipients`() {
        val announcementId = UUID.randomUUID()
        val snapshotId = UUID.randomUUID()
        val announcement = AnnouncementJobDto(420, announcementId, snapshotId)
        every { opsForList.range("snapshot:$snapshotId", 0, -1) } returns listOf(
            TokenOwnershipInfoWithAssetCount("123", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", 1),
            TokenOwnershipInfoWithAssetCount("456", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", 52),
        )
        every { opsForList.rightPushAll("announcements:$announcementId", any<List<AnnouncementRecipientDto>>()) } returns 3
        every { externalAccountRepository.findEligibleAccountsByWallet(420, listOf("123", "456"), listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED)) } returns listOf(40, 41)
        every { externalAccountRepository.findMessagingExternalAccountsForProjectAndAccounts(420, listOf(40, 41), listOf("cardano")) } returns listOf(
            makeExternalAccountWithAccountProjection(40),
            makeExternalAccountWithAccountProjection(41),
        )
        every { externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(420, SubscriptionStatus.SUBSCRIBED) } returns listOf(
            makeExternalAccountWithAccountProjection(42),
        )
        subscriptionServiceVibrant.processSnapshotCompleted(announcement)

        verify(exactly = 1) {
            opsForList.rightPushAll("announcements:$announcementId", match<List<AnnouncementRecipientDto>> {
                it.size == 3 && it[0].externalAccountId == 40L && it[1].externalAccountId == 41L && it[2].externalAccountId == 42L
            })
            redisTemplate.expire("announcements:$announcementId", 48, java.util.concurrent.TimeUnit.HOURS)
        }
        verify(exactly = 1) { rabbitTemplate.convertAndSend("completed", announcement) }
    }

    @Test
    fun `an account that is subscribed through token ownership and explicit subscribed is not messaged twice`() {
        val announcementId = UUID.randomUUID()
        val snapshotId = UUID.randomUUID()
        val announcement = AnnouncementJobDto(420, announcementId, snapshotId)
        every { opsForList.range("snapshot:$snapshotId", 0, -1) } returns listOf(
            TokenOwnershipInfoWithAssetCount("123", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", 1),
            TokenOwnershipInfoWithAssetCount("456", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", 52),
        )
        every { opsForList.rightPushAll("announcements:$announcementId", any<List<AnnouncementRecipientDto>>()) } returns 3
        every { externalAccountRepository.findEligibleAccountsByWallet(420, listOf("123", "456"), listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED)) } returns listOf(40)
        every { externalAccountRepository.findMessagingExternalAccountsForProjectAndAccounts(420, listOf(40), listOf("cardano")) } returns listOf(
            makeExternalAccountWithAccountProjection(40),
        )
        every { externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(420, SubscriptionStatus.SUBSCRIBED) } returns listOf(
            makeExternalAccountWithAccountProjection(40),
        )
        subscriptionServiceVibrant.processSnapshotCompleted(announcement)

        verify(exactly = 1) {
            opsForList.rightPushAll("announcements:$announcementId", match<List<AnnouncementRecipientDto>> {
                it.size == 1 && it[0].externalAccountId == 40L
            })
        }
    }

    @Test
    fun `if no accounts are subscribed no publishing occurs`() {
        // This needs some additional work down the line as the project status is simply orphaned in this case
        val announcementId = UUID.randomUUID()
        val snapshotId = UUID.randomUUID()
        val announcement = AnnouncementJobDto(420, announcementId, snapshotId)
        every { opsForList.range("snapshot:$snapshotId", 0, -1) } returns listOf(
            TokenOwnershipInfoWithAssetCount("123", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", 1),
            TokenOwnershipInfoWithAssetCount("456", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", 52),
        )
        every { opsForList.rightPushAll("announcements:$announcementId", any<List<AnnouncementRecipientDto>>()) } returns 3
        every { externalAccountRepository.findEligibleAccountsByWallet(420, listOf("123", "456"), listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED)) } returns listOf(40)
        every { externalAccountRepository.findMessagingExternalAccountsForProjectAndAccounts(420, listOf(40), listOf("cardano")) } returns emptyList()
        every { externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(420, SubscriptionStatus.SUBSCRIBED) } returns emptyList()
        subscriptionServiceVibrant.processSnapshotCompleted(announcement)

        verify(exactly = 0) {
            opsForList.rightPushAll(any(), any<List<AnnouncementRecipientDto>>())
        }
    }

    fun makeExternalAccountWithAccountProjection(externalAccountId: Long): ExternalAccountWithAccountProjection {
        return object : ExternalAccountWithAccountProjection {
            override val id: Long = externalAccountId
            override val referenceId: String = externalAccountId.toString()
            override val referenceName: String? = null
            override val displayName: String? = null
            override val registrationTime: OffsetDateTime = OffsetDateTime.now()
            override val type: String = "discord"
            override val metadata: ByteArray? = null
            override val accountId: Long = 4
        }
    }
}