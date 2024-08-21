package io.vibrantnet.ryp.core.events.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.*
import io.ryp.cardano.model.EventNotification
import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import io.vibrantnet.ryp.core.events.persistence.DrepVoteDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

internal class DrepVoteServiceTest {
    private val drepVoteDao = mockk<DrepVoteDao>()
    private val cip100Client = mockk<WebClient>()
    private val objectMapper = jacksonObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
    private val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
    private val service = DrepVoteService(
        drepVoteDao,
        cip100Client,
        objectMapper,
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
                DRepVoteDetailsDto(1, "x", 1, "drep", null)
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
                DRepVoteDetailsDto(2, "x", 1, "drep", null)
            ))
        }
        service.checkForNewVotes()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
        assertEquals(setOf(1L, 2L), service.drepVoteIds)
    }
}