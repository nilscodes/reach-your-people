package io.vibrantnet.ryp.core.events.service

import io.mockk.*
import io.ryp.cardano.model.EventNotification
import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import io.vibrantnet.ryp.core.events.persistence.StakepoolDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import reactor.core.publisher.Flux

internal class StakepoolRetirementServiceTest {
    private val stakepoolDao = mockk<StakepoolDao>()
    private val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
    private val service = StakepoolRetirementService(
        stakepoolDao,
        rabbitTemplate
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `on startup, should not send notifications for existing retirements`() {
        every { stakepoolDao.getStakepoolRetirementsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                StakepoolRetirementDto(1, "x", "pool")
            ))
        }
        service.checkForNewRetiredStakepools()
        verify { rabbitTemplate wasNot Called }
        assertEquals(setOf(1L), service.poolRetirementIds)
    }

    @Test
    fun `if a new pool retirement is detected, it should be sent as a notification and added to the list`() {
        service.poolRetirementIds.add(1)
        every { stakepoolDao.getStakepoolRetirementsWithIdsHigherThan(any()) } answers {
            Flux.fromIterable(listOf(
                StakepoolRetirementDto(2, "y", "pool2")
            ))
        }
        service.checkForNewRetiredStakepools()
        verify { rabbitTemplate.convertAndSend("event-notifications", any<EventNotification>()) }
        assertEquals(setOf(1L, 2L), service.poolRetirementIds)
    }
}