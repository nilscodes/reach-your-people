package io.vibrantnet.ryp.core.events.service

import io.mockk.*
import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.governance.GovernanceActionType
import io.vibrantnet.ryp.core.events.model.GovernanceActionProposalDto
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Body
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Model
import io.vibrantnet.ryp.core.events.persistence.GovernanceActionDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class GovernanceActionServiceTest {
    private val governanceActionDao = mockk<GovernanceActionDao>()
    private val cip100Service = mockk<Cip100Service>()
    private val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
    private val service = GovernanceActionService(
        governanceActionDao,
        cip100Service,
        rabbitTemplate
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `on startup, should not send notifications for existing proposals`() {
        every { governanceActionDao.getGovernanceActionsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                GovernanceActionProposalDto(1, "x", 0, GovernanceActionType.COMMITTEE_UPDATE, null)
            ))
        }
        service.checkForNewGovernanceActionProposals()
        verify { rabbitTemplate wasNot Called }
        assertEquals(setOf(1L), service.governanceActionIds)
    }

    @Test
    fun `if a new proposal is detected, it should be sent as a notification and added to the list`() {
        service.governanceActionIds.add(1)
        every { governanceActionDao.getGovernanceActionsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                GovernanceActionProposalDto(2, "x", 0, GovernanceActionType.COMMITTEE_UPDATE, null)
            ))
        }
        service.checkForNewGovernanceActionProposals()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
        assertEquals(setOf(1L, 2L), service.governanceActionIds)
    }

    @Test
    fun `if anchor is present and valid, should fetch title if present`() {
        service.governanceActionIds.add(1)
        val proposal = GovernanceActionProposalDto(2, "x", 0, GovernanceActionType.COMMITTEE_UPDATE, "anchor")
        every { governanceActionDao.getGovernanceActionsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(proposal))
        }
        every { cip100Service.getCip100Document(proposal.votingAnchorUrl!!) } answers {
            Mono.just(Cip100Model(Cip100Body(title = "oh yeah", comment = "comment")))
        }
        service.checkForNewGovernanceActionProposals()
        verify { rabbitTemplate.convertAndSend("event-notifications", match<EventNotification> { it.metadata["title"] == "oh yeah" }) }
    }

    @Test
    fun `if anchor is present and valid, should fetch comment if no title present`() {
        service.governanceActionIds.add(1)
        val proposal = GovernanceActionProposalDto(2, "x", 0, GovernanceActionType.COMMITTEE_UPDATE, "anchor")
        every { governanceActionDao.getGovernanceActionsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(proposal))
        }
        every { cip100Service.getCip100Document(proposal.votingAnchorUrl!!) } answers {
            Mono.just(Cip100Model(Cip100Body(comment = "comment")))
        }
        service.checkForNewGovernanceActionProposals()
        verify { rabbitTemplate.convertAndSend("event-notifications", match<EventNotification> { it.metadata["title"] == "comment" }) }
    }

    @Test
    fun `if anchor is present but errors out, should still send notification`() {
        service.governanceActionIds.add(1)
        val proposal = GovernanceActionProposalDto(2, "x", 0, GovernanceActionType.COMMITTEE_UPDATE, "anchor")
        every { governanceActionDao.getGovernanceActionsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(proposal))
        }
        every { cip100Service.getCip100Document(proposal.votingAnchorUrl!!) } answers { Mono.error(WebClientResponseException(404, "bad stuff happened, sorry", null, null, null)) }
        service.checkForNewGovernanceActionProposals()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
    }
}