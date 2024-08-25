package io.vibrantnet.ryp.core.verification.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.verification.persistence.DrepDao
import io.vibrantnet.ryp.core.verification.persistence.StakepoolDao
import io.vibrantnet.ryp.core.verification.persistence.TokenDao
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class StakeApiServiceVibrantTest {
    private val tokenDao = mockk<TokenDao>()
    private val stakepoolDao = mockk<StakepoolDao>()
    private val drepDao = mockk<DrepDao>()
    private val service = StakeApiServiceVibrant(tokenDao, stakepoolDao, drepDao)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getMultiAssetCountForStakeAddress returns the correct value`() {
        val stakeAddress = "stakeAddress"
        val expected = listOf(
            TokenOwnershipInfoWithAssetCount(
                "policyId",
                "assetName",
                1
            ),
            TokenOwnershipInfoWithAssetCount(
                "policyId2",
                "assetName2",
                2
            )
        )
        every { tokenDao.getMultiAssetListForStakeAddress(stakeAddress) } returns expected

        val result = service.getMultiAssetCountForStakeAddress(stakeAddress)

        StepVerifier.create(result)
            .expectNext(expected[0])
            .expectNext(expected[1])
            .verifyComplete()
    }

    @Test
    fun `getStakepoolDetailsForStakeAddress returns the correct value`() {
        val stakeAddress = "stakeAddress"
        val stakepoolDetails = StakepoolDetailsDto("poolId", "poolName", "poolTicker", "poolDescription", "poolHomepage")
        every { stakepoolDao.getStakepoolDetailsForStakeAddress(stakeAddress) } answers {
            Mono.just(stakepoolDetails)
        }

        val result = service.getStakepoolDetailsForStakeAddress(stakeAddress)

        StepVerifier.create(result)
            .expectNext(stakepoolDetails)
            .verifyComplete()
    }
}