package io.vibrantnet.ryp.core.publishing.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.ryp.shared.model.StatisticsDto
import io.ryp.shared.model.StatisticsUpdateDto
import io.vibrantnet.ryp.core.publishing.model.AnnouncementStatus
import io.vibrantnet.ryp.core.publishing.model.Statistics
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsUpdateService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

internal class StatisticsServiceVibrantTest {

    private val announcementsUpdateService = mockk<AnnouncementsUpdateService>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `processing a single statistics method from discord works`() {
        processSingleMessage("discord")
    }

    @Test
    fun `processing a single statistics method from sms works`() {
        processSingleMessage("sms")
    }

    @Test
    fun `processing a single statistics method from pushapi works`() {
        processSingleMessage("pushapi")
    }

    private fun processSingleMessage(type: String) {
        val statisticsService = StatisticsServiceVibrant(announcementsUpdateService)

        val uuid = UUID.randomUUID()
        val update = makeStatisticsUpdateDto(uuid)
        every { announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), any(), any()) } answers { Mono.empty() }

        when(type) {
            "discord" -> statisticsService.processDiscordStatistics(update)
            "sms" -> statisticsService.processSmsStatistics(update)
            "pushapi" -> statisticsService.processPushApiStatistics(update)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
        val processedUpdates = statisticsService.processBufferedMessagesReactive()

        StepVerifier.create(processedUpdates)
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), Statistics(
                sent = emptyMap(),
                uniqueAccounts = 0,
                explicitSubscribers = 0,
                delivered = mapOf(type to 1),
                failures = mapOf(type to 2),
                views = mapOf(type to 3)
            ), isNull<AnnouncementStatus>())
        }
    }

    @Test
    fun `merging two statistics for one service works`() {
        val statisticsService = StatisticsServiceVibrant(announcementsUpdateService)

        val uuid = UUID.randomUUID()
        val update1 = makeStatisticsUpdateDto(uuid)
        val update2 = makeStatisticsUpdateDto(uuid)
        every { announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), any(), any()) } answers { Mono.empty() }

        statisticsService.processDiscordStatistics(update1)
        statisticsService.processDiscordStatistics(update2)
        val processedUpdates = statisticsService.processBufferedMessagesReactive()

        StepVerifier.create(processedUpdates)
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), Statistics(
                sent = emptyMap(),
                uniqueAccounts = 0,
                explicitSubscribers = 0,
                delivered = mapOf("discord" to 2),
                failures = mapOf("discord" to 4),
                views = mapOf("discord" to 6)
            ), isNull<AnnouncementStatus>())
        }
    }

    @Test
    fun `merging two partially populated statistics works`() {
        val statisticsService = StatisticsServiceVibrant(announcementsUpdateService)

        val uuid = UUID.randomUUID()
        val update1 = makeStatisticsUpdateDto(uuid).copy(statistics = StatisticsDto(failures = 1))
        val update2 = makeStatisticsUpdateDto(uuid).copy(statistics = StatisticsDto(delivered = 4))
        every { announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), any(), any()) } answers { Mono.empty() }

        statisticsService.processDiscordStatistics(update1)
        statisticsService.processDiscordStatistics(update2)
        val processedUpdates = statisticsService.processBufferedMessagesReactive()

        StepVerifier.create(processedUpdates)
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), Statistics(
                sent = emptyMap(),
                uniqueAccounts = 0,
                explicitSubscribers = 0,
                delivered = mapOf("discord" to 4),
                failures = mapOf("discord" to 1),
                views = mapOf("discord" to 0)
            ), isNull<AnnouncementStatus>())
        }
    }

    @Test
    fun `maximum buffer is respected`() {
        val statisticsService = StatisticsServiceVibrant(announcementsUpdateService)

        val uuid = UUID.randomUUID()
        val update = makeStatisticsUpdateDto(uuid)
        every { announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), any(), any()) } answers { Mono.empty() }

        for (i in 1..maxMessagesPerBatch + 1) {
            statisticsService.processDiscordStatistics(update)
        }
        val processedUpdates = statisticsService.processBufferedMessagesReactive()

        StepVerifier.create(processedUpdates)
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), Statistics(
                sent = emptyMap(),
                uniqueAccounts = 0,
                explicitSubscribers = 0,
                delivered = mapOf("discord" to maxMessagesPerBatch.toLong()),
                failures = mapOf("discord" to 2 * maxMessagesPerBatch.toLong()),
                views = mapOf("discord" to 3 * maxMessagesPerBatch.toLong())
            ), isNull<AnnouncementStatus>())
        }

        val secondUpdateBatch = statisticsService.processBufferedMessagesReactive()

        StepVerifier.create(secondUpdateBatch)
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            announcementsUpdateService.updateAnnouncementStatistics(uuid.toString(), Statistics(
                sent = emptyMap(),
                uniqueAccounts = 0,
                explicitSubscribers = 0,
                delivered = mapOf("discord" to 1),
                failures = mapOf("discord" to 2),
                views = mapOf("discord" to 3)
            ), isNull<AnnouncementStatus>())
        }
    }

    @Test
    fun `no statistics are processed when buffer is empty`() {
        val statisticsService = StatisticsServiceVibrant(announcementsUpdateService)
        val processedUpdates = statisticsService.processBufferedMessagesReactive()

        StepVerifier.create(processedUpdates)
            .expectComplete()
            .verify()

        verify(exactly = 0) {
            announcementsUpdateService.updateAnnouncementStatistics(any(), any(), any())
        }
    }

    private fun makeStatisticsUpdateDto(uuid: UUID) = StatisticsUpdateDto(
        announcementId = uuid,
        statistics = StatisticsDto(
            delivered = 1,
            failures = 2,
            views = 3
        )
    )
}