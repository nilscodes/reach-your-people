package io.vibrantnet.ryp.core.events.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.vibrantnet.ryp.core.events.model.GovernanceActionProposalDto
import io.vibrantnet.ryp.core.events.persistence.GovernanceActionDao
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class GovernanceActionService(
    private val governanceActionDao: GovernanceActionDao,
    private val cip100Service: Cip100Service,
    private val rabbitTemplate: RabbitTemplate,
    val governanceActionIds: MutableSet<Long> = mutableSetOf(),
) {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    fun checkForNewGovernanceActionProposals() {
        val newestAction = governanceActionIds.maxOrNull() ?: 0
        logger.info { "Checking for new governance actions with ID higher than $newestAction" }
        val newActions = governanceActionDao.getGovernanceActionsWithIdsHigherThan(newestAction)
        if (newestAction > 0) { // For now don't send notifications for all actions that exist in the database before the start of the service. Will be done via persisting last announced proposal in the future
            newActions.map {
                logger.info { "Processing new action $it" }
                var proposalInfo: String? = null
                try {
                    proposalInfo = getProposalInfo(it)
                    logger.info { "Proposal info: $proposalInfo" }
                } catch (e: Exception) {
                    logger.warn(e) { "Error processing proposal info for proposal ${it.proposalId}, proceeding without title or comment" }
                }
                rabbitTemplate.convertAndSend("event-notifications", it.toEventNotification(proposalInfo))
                governanceActionIds.add(it.proposalId)
            }.subscribe()
        } else {
            governanceActionIds.addAll(newActions.map { it.proposalId }.toIterable())
        }
    }

    private fun getProposalInfo(governanceActionProposal: GovernanceActionProposalDto): String? {
        return if (governanceActionProposal.votingAnchorUrl != null) {
            cip100Service.getCip100Document(governanceActionProposal.votingAnchorUrl)
                .map { (it.body.title ?: (it.body.comment ?: "")) }
                .block()
        } else {
            null
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}