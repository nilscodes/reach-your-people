package io.vibrantnet.ryp.core.verification.service

import io.mockk.*
import io.ryp.cardano.model.*
import io.ryp.shared.model.AnnouncementJobDto
import io.vibrantnet.ryp.core.verification.persistence.DrepDao
import io.vibrantnet.ryp.core.verification.persistence.StakepoolDao
import io.vibrantnet.ryp.core.verification.persistence.TokenDao
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import java.util.*

internal class SnapshotServiceVibrantTest {
    private val tokenDao = mockk<TokenDao>()
    private val stakepoolDao = mockk<StakepoolDao>()
    private val drepDao = mockk<DrepDao>()
    private val redisTemplate = mockk<RedisTemplate<String, Any>>()
    private val rabbitTemplate = mockk<RabbitTemplate>()
    private val service = SnapshotServiceVibrant(tokenDao, stakepoolDao, drepDao, redisTemplate, rabbitTemplate)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `snapshot information gets pushed to redis from both policies and stake pools if present`() {
        val announcementUuid = UUID.randomUUID()
        every { tokenDao.getMultiAssetCountSnapshotForPolicyId(any()) } returns listOf(
            TokenOwnershipInfoWithAssetCount("stakeAddress1", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", 1),
        )
        every { stakepoolDao.getActiveDelegationWithoutAmount(any()) } returns listOf(
            StakepoolDelegationInfoDto("poolHash", 1, "stakeAddress2")
        )
        every { drepDao.getActiveDelegationWithoutAmount(any()) } returns listOf(
            DRepDelegationInfoDto("drepid", 1, "stakeAddress3")
        )
        val opsForList = mockk<ListOperations<String, Any>>()
        every { redisTemplate.opsForList() } returns opsForList
        every { opsForList.rightPushAll(any(), any()) } returns 2
        every { redisTemplate.expire(any(), any(), any()) } returns true
        every { rabbitTemplate.convertAndSend("snapshotcompleted", any<SnapshotRequestDto>()) } just Runs

        service.processSnapshot(SnapshotRequestDto(AnnouncementJobDto(12, announcementUuid, null), listOf("4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f"), listOf("poolHash"), listOf("drepid")))

        verify(exactly = 1) {
            opsForList.rightPushAll(match { it.startsWith("snapshot:") }, eq(listOf(
                SnapshotStakeAddressDto("stakeAddress1", SnapshotType.POLICY),
                SnapshotStakeAddressDto("stakeAddress2", SnapshotType.STAKEPOOL),
                SnapshotStakeAddressDto("stakeAddress3", SnapshotType.DREP)
            )))
        }
        verify(exactly = 1) {
            redisTemplate.expire(match { it.startsWith("snapshot:") }, 48, java.util.concurrent.TimeUnit.HOURS)
        }
        verify(exactly = 1) {
            rabbitTemplate.convertAndSend("snapshotcompleted", match<AnnouncementJobDto> { it.snapshotId != null })
        }
    }

    @Test
    fun `redis does not get any information if snapshot is empty`() {
        val announcementUuid = UUID.randomUUID()
        every { tokenDao.getMultiAssetCountSnapshotForPolicyId(any()) } returns emptyList()
        every { stakepoolDao.getActiveDelegationWithoutAmount(any()) } returns emptyList()
        every { drepDao.getActiveDelegationWithoutAmount(any()) } returns emptyList()
        val opsForList = mockk<ListOperations<String, Any>>()
        every { redisTemplate.opsForList() } returns opsForList
        every { opsForList.rightPushAll(any(), any()) } returns 0
        every { redisTemplate.expire(any(), any(), any()) } returns true
        every { rabbitTemplate.convertAndSend("snapshotcompleted", any<SnapshotRequestDto>()) } just Runs

        service.processSnapshot(SnapshotRequestDto(AnnouncementJobDto(12, announcementUuid, null), listOf("4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f"), listOf("poolHash"), listOf("drepid")))

        verify(exactly = 0) {
            opsForList.rightPushAll(any(), any())
        }
        verify(exactly = 0) {
            redisTemplate.expire(any(), any(), any())
        }
        verify(exactly = 1) {
            rabbitTemplate.convertAndSend("snapshotcompleted", match<AnnouncementJobDto> { it.snapshotId != null })
        }
    }
}