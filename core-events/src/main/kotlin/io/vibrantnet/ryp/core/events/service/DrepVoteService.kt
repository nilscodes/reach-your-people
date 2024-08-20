package io.vibrantnet.ryp.core.events.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Model
import io.vibrantnet.ryp.core.events.persistence.DrepVoteDao
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

val logger = KotlinLogging.logger {}

@Service
class DrepVoteService(
    private val drepVoteDao: DrepVoteDao,
    @Qualifier("cip100Client") private val cip100Client: WebClient,
    private val objectMapper: ObjectMapper,
    private val rabbitTemplate: RabbitTemplate,
    val drepVoteIds: MutableSet<Long> = mutableSetOf(),
) {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    fun checkForNewVotes() {
        val newestVote = drepVoteIds.maxOrNull() ?: 0
        logger.info { "Checking for new votes with ID higher than $newestVote" }
        val newVotes = drepVoteDao.getDrepVotesWithIdsHigherThan(newestVote)
        if (newestVote > 0) { // For now don't send notifications for all votes that exist in the database before the start of the service. Will be done via persisting last announced vote in the future
            newVotes.map {
                logger.info { "Processing vote $it" }
                var voteComment: String? = null
                try {
                    voteComment = getVoteComment(it)
                    logger.info { "Vote comment: $voteComment" }
                } catch (e: Exception) {
                    logger.warn(e) { "Error processing vote comment for vote ${it.id}, proceeding without comment" }
                }
                rabbitTemplate.convertAndSend("event-notifications", it.toEventNotification(voteComment))
                drepVoteIds.add(it.id)
            }.subscribe()
        } else {
            drepVoteIds.addAll(newVotes.map { it.id }.toIterable())
        }
    }

    private fun getVoteComment(voteDetailsDto: DRepVoteDetailsDto): String? {
        return if (voteDetailsDto.votingAnchorUrl != null) {
            cip100Client.get()
                .uri(voteDetailsDto.votingAnchorUrl)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM, MediaType.valueOf("binary/octet-stream"))
                .exchangeToMono { response ->
                    val contentType = response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM)

                    if (contentType.includes(MediaType.APPLICATION_JSON)) {
                        response.bodyToMono(Cip100Model::class.java)
                    } else if (contentType.includes(MediaType.APPLICATION_OCTET_STREAM) || contentType.includes(MediaType.valueOf("binary/octet-stream"))) {
                        response.bodyToMono(DataBuffer::class.java)
                            .map { buffer ->
                                val content = buffer.readableByteBuffers().asSequence()
                                    .map { byteBuffer -> StandardCharsets.UTF_8.decode(byteBuffer).toString() }
                                    .joinToString("")
                                objectMapper.readValue(content, Cip100Model::class.java)
                            }
                    } else {
                        Mono.error(UnsupportedOperationException("Unsupported content type: $contentType"))
                    }
                }
                .map { it.body.comment }
                .block() // TODO REALLY BLOCK?
        } else {
            null
        }
    }
}