package io.vibrantnet.ryp.core.events.service

import io.mockk.*
import io.ryp.cardano.model.EventNotification
import io.vibrantnet.ryp.core.events.model.StakepoolVoteDetailsDto
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Body
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Model
import io.vibrantnet.ryp.core.events.persistence.StakepoolDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class StakepoolVoteServiceTest {
    private val stakepoolDao = mockk<StakepoolDao>()
    private val cip100Service = mockk<Cip100Service>()
    private val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
    private val service = StakepoolVoteService(
        stakepoolDao,
        cip100Service,
        rabbitTemplate
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `on startup, should not send notifications for existing votes`() {
        every { stakepoolDao.getStakepoolVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                StakepoolVoteDetailsDto(1, "x", 0, 1, "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4", null)
            ))
        }
        service.checkForNewVotes()
        verify { rabbitTemplate wasNot Called }
        assertEquals(setOf(1L), service.stakepoolVoteIds)
    }

    @Test
    fun `if a new vote is detected, it should be sent as a notification and added to the list`() {
        service.stakepoolVoteIds.add(1)
        every { stakepoolDao.getStakepoolVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                StakepoolVoteDetailsDto(2, "x", 0, 1, "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4", null)
            ))
        }
        service.checkForNewVotes()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
        assertEquals(setOf(1L, 2L), service.stakepoolVoteIds)
    }

    @Test
    fun `if anchor is present and valid, should fetch comment`() {
        service.stakepoolVoteIds.add(1)
        val vote = StakepoolVoteDetailsDto(2, "x", 0, 1, "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4", "anchor")
        every { stakepoolDao.getStakepoolVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(vote))
        }
        every { cip100Service.getCip100Document(vote.votingAnchorUrl!!) } answers {
            Mono.just(Cip100Model(Cip100Body(comment = "oh yeah")))
        }
        service.checkForNewVotes()
        verify { rabbitTemplate.convertAndSend("event-notifications", match<EventNotification> { it.metadata["comment"] == "oh yeah" }) }
    }

    @Test
    fun `if anchor is present but errors out, should still send notification`() {
        service.stakepoolVoteIds.add(1)
        val vote = StakepoolVoteDetailsDto(2, "x", 0, 1, "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4", "anchor")
        every { stakepoolDao.getStakepoolVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(vote))
        }
        every { cip100Service.getCip100Document(vote.votingAnchorUrl!!) } answers { Mono.error(
            WebClientResponseException(404, "bad stuff happened, sorry", null, null, null)
        ) }
        service.checkForNewVotes()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
    }
}