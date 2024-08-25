package io.vibrantnet.ryp.core.events.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import io.vibrantnet.ryp.core.events.persistence.DrepVoteDao
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class DrepVoteService(
    private val drepVoteDao: DrepVoteDao,
    private val cip100Service: Cip100Service,
    private val rabbitTemplate: RabbitTemplate,
    val drepVoteIds: MutableSet<Long> = mutableSetOf(),
) {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    fun checkForNewVotes() {
        val newestVote = drepVoteIds.maxOrNull() ?: 0
        logger.info { "Checking for new dRep votes with ID higher than $newestVote" }
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
            cip100Service.getCip100Document(voteDetailsDto.votingAnchorUrl)
                .map { it.body.comment ?: "" }
                .block()
        } else {
            null
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}