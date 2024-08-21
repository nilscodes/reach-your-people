package io.vibrantnet.ryp.core.events.service

import io.vibrantnet.ryp.core.events.persistence.StakepoolDao
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StakepoolRetirementService(
    private val stakepoolDao: StakepoolDao,
    private val rabbitTemplate: RabbitTemplate,
    val poolRetirementIds: MutableSet<Long> = mutableSetOf(),
) {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    fun checkForNewRetiredStakepools() {
        val newestRetirement = poolRetirementIds.maxOrNull() ?: 0
        logger.info { "Checking for new stake pool retirements with IDs higher than $newestRetirement" }
        val newPoolRetirements = stakepoolDao.getStakepoolRetirementsWithIdsHigherThan(newestRetirement)
        if (newestRetirement > 0) { // For now don't send notifications for all retirements that exist in the database before the start of the service. Will be done via persisting last retired reported pool in the future
            newPoolRetirements.map {
                logger.info { "Processing pool retirement $it" }
                rabbitTemplate.convertAndSend("event-notifications", it.toEventNotification())
                poolRetirementIds.add(it.id)
            }.subscribe()
        } else {
            poolRetirementIds.addAll(newPoolRetirements.map { it.id }.toIterable())
        }
    }
}