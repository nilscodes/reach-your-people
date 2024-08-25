package io.vibrantnet.ryp.core.publishing.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.cardano.model.governance.GovernanceActionType
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import reactor.core.publisher.Mono

internal class EventsServiceVibrantTest {
    private val redirectService = mockk<RedirectService>()
    private val announcementsApiServiceVibrant = mockk<AnnouncementsApiServiceVibrant>()
    private val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
    private val redisTemplate = mockk<RedisTemplate<String, Any>>()
    private val opsForValue = mockk<ValueOperations<String, Any>>(relaxed = true)
    private val verifyService = mockk<VerifyService>()
    private val eventsServiceVibrant = EventsServiceVibrant(redirectService, announcementsApiServiceVibrant, rabbitTemplate, redisTemplate, verifyService)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { redisTemplate.opsForValue() } returns opsForValue
        // Assume the basic capabilities just work
        every { redirectService.createShortUrlWithFallback(any()) } answers { Mono.just("https://go.ryp.io/works") }
        every { announcementsApiServiceVibrant.createAnnouncement(any(), any()) } answers { Mono.just(announcementFromBasicAnnouncement(firstArg<BasicAnnouncementWithIdDto>(), 0, CorePublishingConfiguration("", "", "", "https://test.ryp.io"))) }
    }

    @Test
    fun `processing an event notification for a dRep vote works correctly`() {
        val drepVoteNotification = EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
            transactionIndex = 0,
            audience = Audience(
                dreps = listOf("drep1")
            ),
            metadata = mapOf("comment" to "sounds good")
        )
        eventsServiceVibrant.receiveMessage(drepVoteNotification)
        verify(exactly = 1) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 1) {
            opsForValue.set(any(), match<BasicAnnouncementWithIdDto> {
                it.type == AnnouncementType.GOVERNANCE_VOTE
                        && it.author == 0L
                        && it.title == ""
                        && it.content == ""
                        && it.link == "https://go.ryp.io/works"
                        && it.externalLink == ""
                        && (it.policies?.isEmpty() ?: false)
                        && (it.stakepools?.isEmpty() ?: false)
                        && it.dreps == listOf("drep1")
                        && it.global.isEmpty()
                        && it.metadata == mapOf(
                            "drepId" to "drep1",
                            "poolHash" to "",
                            "comment" to "sounds good",
                            "transactionHash" to "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
                            "transactionIndex" to "0"
                        )
            }, any(), any())
        }
    }

    @Test
    fun `processing an event notification for a SPO vote works correctly`() {
        val spoVoteNotification = EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
            transactionIndex = 0,
            audience = Audience(
                stakepools = listOf("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4")
            ),
            metadata = mapOf("comment" to "sounds great")
        )
        eventsServiceVibrant.receiveMessage(spoVoteNotification)
        verify(exactly = 1) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 1) {
            opsForValue.set(any(), match<BasicAnnouncementWithIdDto> {
                it.type == AnnouncementType.GOVERNANCE_VOTE
                        && it.author == 0L
                        && it.title == ""
                        && it.content == ""
                        && it.link == "https://go.ryp.io/works"
                        && it.externalLink == ""
                        && (it.policies?.isEmpty() ?: false)
                        && it.stakepools == listOf("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4")
                        && (it.dreps?.isEmpty() ?: false)
                        && it.global.isEmpty()
                        && it.metadata == mapOf(
                            "drepId" to "",
                            "poolHash" to "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4",
                            "comment" to "sounds great",
                            "transactionHash" to "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
                            "transactionIndex" to "0"
                        )
            }, any(), any())
        }
    }

    @Test
    fun `processing an event notification for an SPO retirement works correctly`() {
        val spoRetirementNotification = EventNotification(
            type = EventNotificationType.STAKEPOOL_RETIREMENT,
            transactionHash = "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
            transactionIndex = 0,
            audience = Audience(
                stakepools = listOf("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4")
            )
        )
        val poolDetails = StakepoolDetailsDto("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4", "HAZEL", "Hazelpool", "https://www.hazelpool.com", "A pool that supports cat fostering")
        every { verifyService.getStakepoolDetails("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4") } answers { Mono.just(poolDetails) }
        eventsServiceVibrant.receiveMessage(spoRetirementNotification)
        verify(exactly = 1) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 1) {
            opsForValue.set(any(), match<BasicAnnouncementWithIdDto> {
                it.type == AnnouncementType.STAKEPOOL_RETIREMENT
                        && it.author == 0L
                        && it.title == ""
                        && it.content == ""
                        && it.link == "https://go.ryp.io/works"
                        && it.externalLink == ""
                        && (it.policies?.isEmpty() ?: false)
                        && it.stakepools == listOf("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4")
                        && (it.dreps?.isEmpty() ?: false)
                        && it.global.isEmpty()
                        && it.metadata == mapOf(
                            "poolHash" to "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4",
                            "poolName" to "Hazelpool",
                            "poolTicker" to "HAZEL",
                            "transactionHash" to "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
                            "transactionIndex" to "0"
                        )
            }, any(), any())
        }
    }

    @Test
    fun `processing an event notification for a new governance proposal works correctly`() {
        val spoRetirementNotification = EventNotification(
            type = EventNotificationType.GOVERNANCE_ACTION_NEW_PROPOSAL,
            transactionHash = "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
            transactionIndex = 0,
            audience = Audience(
                global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO)
            ),
            metadata = mapOf(
                "type" to GovernanceActionType.INFO.name,
                "title" to "Cool Proposal"
            )
        )
        val poolDetails = StakepoolDetailsDto("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4", "HAZEL", "Hazelpool", "https://www.hazelpool.com", "A pool that supports cat fostering")
        every { verifyService.getStakepoolDetails("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4") } answers { Mono.just(poolDetails) }
        eventsServiceVibrant.receiveMessage(spoRetirementNotification)
        verify(exactly = 1) { rabbitTemplate.convertAndSend("announcements", any<AnnouncementJobDto>()) }
        verify(exactly = 1) {
            opsForValue.set(any(), match<BasicAnnouncementWithIdDto> {
                it.type == AnnouncementType.GOVERNANCE_ACTION_NEW_PROPOSAL
                        && it.author == 0L
                        && it.title == ""
                        && it.content == ""
                        && it.link == "https://go.ryp.io/works"
                        && it.externalLink == ""
                        && (it.policies?.isEmpty() ?: false)
                        && (it.stakepools?.isEmpty() ?: false)
                        && (it.dreps?.isEmpty() ?: false)
                        && it.global == listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO)
                        && it.metadata == mapOf(
                            "proposalType" to "INFO",
                            "title" to "Cool Proposal",
                            "transactionHash" to "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
                            "transactionIndex" to "0"
                        )
            }, any(), any())
        }
    }
}