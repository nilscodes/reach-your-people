package io.vibrantnet.ryp.core.events.service

import io.mockk.*
import io.ryp.cardano.model.EventNotification
import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Body
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Model
import io.vibrantnet.ryp.core.events.persistence.DrepVoteDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class DrepVoteServiceTest {
    private val drepVoteDao = mockk<DrepVoteDao>()
    private val cip100Service = mockk<Cip100Service>()
    private val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
    private val service = DrepVoteService(
        drepVoteDao,
        cip100Service,
        rabbitTemplate
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `on startup, should not send notifications for existing votes`() {
        every { drepVoteDao.getDrepVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                DRepVoteDetailsDto(1, "x", 0, 1, "drep", null)
            ))
        }
        service.checkForNewVotes()
        verify { rabbitTemplate wasNot Called }
        assertEquals(setOf(1L), service.drepVoteIds)
    }

    @Test
    fun `if a new vote is detected, it should be sent as a notification and added to the list`() {
        service.drepVoteIds.add(1)
        every { drepVoteDao.getDrepVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                DRepVoteDetailsDto(2, "x", 0, 1, "drep", null)
            ))
        }
        service.checkForNewVotes()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
        assertEquals(setOf(1L, 2L), service.drepVoteIds)
    }

    @Test
    fun `if anchor is present and valid, should fetch comment`() {
        service.drepVoteIds.add(1)
        val vote = DRepVoteDetailsDto(2, "x", 0, 1, "drep", "anchor")
        every { drepVoteDao.getDrepVotesWithIdsHigherThan(any()) } answers {
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
        service.drepVoteIds.add(1)
        val vote = DRepVoteDetailsDto(2, "x", 0, 1, "drep", "anchor")
        every { drepVoteDao.getDrepVotesWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(vote))
        }
        every { cip100Service.getCip100Document(vote.votingAnchorUrl!!) } answers { Mono.error(WebClientResponseException(404, "bad stuff happened, sorry", null, null, null)) }
        service.checkForNewVotes()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
    }
}