package io.vibrantnet.ryp.core.publishing.service

import com.mongodb.client.result.UpdateResult
import io.ryp.shared.model.StatisticsUpdateDto
import io.ryp.shared.model.StatisticsUpdateWithTypeDto
import io.vibrantnet.ryp.core.publishing.model.Statistics
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsUpdateService
import io.vibrantnet.ryp.core.publishing.persistence.mergeStatistics
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

const val maxMessagesPerBatch = 1000

@Service
class StatisticsServiceVibrant(
    private val announcementsUpdateService: AnnouncementsUpdateService,
) : StatisticsService {
    private val messageBuffer = ConcurrentLinkedQueue<StatisticsUpdateWithTypeDto>()

    override fun processStatisticsUpdate(type: String, statisticsUpdateDto: StatisticsUpdateDto) {
        messageBuffer.add(statisticsUpdateDto.withType(type))
    }

    @RabbitListener(queues = ["statistics-discord"])
    fun processDiscordStatistics(statisticsUpdateDto: StatisticsUpdateDto) {
        processStatisticsUpdate("discord", statisticsUpdateDto)
    }

    @RabbitListener(queues = ["statistics-sms"])
    fun processSmsStatistics(statisticsUpdateDto: StatisticsUpdateDto) {
        processStatisticsUpdate("sms", statisticsUpdateDto)
    }

    @RabbitListener(queues = ["statistics-pushapi"])
    fun processPushApiStatistics(statisticsUpdateDto: StatisticsUpdateDto) {
        processStatisticsUpdate("pushapi", statisticsUpdateDto)
    }

    @RabbitListener(queues = ["statistics-email"])
    fun processEmailStatistics(statisticsUpdateDto: StatisticsUpdateDto) {
        processStatisticsUpdate("email", statisticsUpdateDto)
    }

    @RabbitListener(queues = ["statistics-telegram"])
    fun processTelegramStatistics(statisticsUpdateDto: StatisticsUpdateDto) {
        processStatisticsUpdate("telegram", statisticsUpdateDto)
    }

    @Scheduled(fixedDelayString = "10", timeUnit = TimeUnit.SECONDS)
    fun processBufferedMessages() {
        processBufferedMessagesReactive().subscribe()
    }

    fun processBufferedMessagesReactive(): Flux<UpdateResult> {
        logger.debug { "Processing ${messageBuffer.size} buffered statistics messages"}
        if (messageBuffer.isEmpty()) {
            return Flux.empty()
        }

        val messages = mutableListOf<StatisticsUpdateWithTypeDto>()
        while (messageBuffer.isNotEmpty() && messages.size < maxMessagesPerBatch) {
            messages.add(messageBuffer.poll())
        }
        logger.debug { "Allowed ${messages.size} statistics messages for processing" }

        val groupedStatistics = messages.groupBy { it.announcementId }
            .mapValues { entry ->
                entry.value.fold(Statistics()) { acc, msg ->
                    mergeStatistics(acc, Statistics(
                        delivered = mapOf(msg.type to (msg.statistics.delivered ?: 0)),
                        failures = mapOf(msg.type to (msg.statistics.failures ?: 0)),
                        views = mapOf(msg.type to (msg.statistics.views ?: 0)),
                    ))
                }
            }

        logger.debug { "Logging statistics for the following announcements: ${groupedStatistics.keys}, which included the following messaging integration sources: ${messages.map { it.type }.toSet()}" }

        return Flux.fromIterable(groupedStatistics.entries)
            .flatMap { (announcementId, stats) ->
                announcementsUpdateService.updateAnnouncementStatistics(announcementId.toString(), stats, null)
            }
    }

}