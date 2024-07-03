package io.vibrantnet.ryp.core.points.service

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.points.persistence.PointsToken
import io.vibrantnet.ryp.core.points.persistence.PointsTokenRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.*

class TokensApiServiceVibrantTest {

    @Test
    fun `list all tokens available works`() {
        val pointsTokenRepository = mockk<PointsTokenRepository>()
        val tokensApiServiceVibrant = TokensApiServiceVibrant(pointsTokenRepository)
        every { pointsTokenRepository.findAll() } returns listOf(
            makeGlobalToken(),
            makeProjectToken(),
        )
        val tokens = tokensApiServiceVibrant.listPointsTokens()
        StepVerifier.create(tokens)
            .expectNext(makeGlobalToken().toDto())
            .expectNext(makeProjectToken().toDto())
            .verifyComplete()
    }

    @Test
    fun `getting an individual points token works`() {
        val pointsTokenRepository = mockk<PointsTokenRepository>()
        val tokensApiServiceVibrant = TokensApiServiceVibrant(pointsTokenRepository)
        val token = makeGlobalToken()
        every { pointsTokenRepository.findById(token.id!!) } returns Optional.of(token)
        val tokenDto = tokensApiServiceVibrant.getPointsToken(token.id!!)
        StepVerifier.create(tokenDto)
            .expectNext(token.toDto())
            .verifyComplete()
    }

    @Test
    fun `creating a points token works and does not let you define create and modify time`() {
        val pointsTokenRepository = mockk<PointsTokenRepository>()
        val tokensApiServiceVibrant = TokensApiServiceVibrant(pointsTokenRepository)
        val token = makeGlobalToken()
        val tokenDto = token.toDto().copy(
            createTime = OffsetDateTime.parse("2019-01-01T00:00:00Z"),
            modifyTime = OffsetDateTime.parse("2019-01-01T00:01:00Z"),
        )
        every { pointsTokenRepository.save(any()) } returns token
        val createdToken = tokensApiServiceVibrant.createPointsToken(tokenDto)
        StepVerifier.create(createdToken)
            .assertNext {
                assertEquals(tokenDto.creator, it.creator)
                assertEquals(tokenDto.name, it.name)
                assertEquals(tokenDto.displayName, it.displayName)
                assertEquals(tokenDto.projectId, it.projectId)
                assertNotEquals(tokenDto.createTime, it.createTime)
                assertNotEquals(tokenDto.modifyTime, it.modifyTime)
            }
            .verifyComplete()
    }


    private fun makeGlobalToken() = PointsToken(
        id = 1,
        name = "TST",
        displayName = "A test token",
        creator = 1,
        projectId = null,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        modifyTime = OffsetDateTime.parse("2021-01-01T00:01:00Z"),
    )

    private fun makeProjectToken() = PointsToken(
        id = 2,
        name = "PRJ",
        displayName = "A project token",
        creator = 2,
        projectId = 5999,
        createTime = OffsetDateTime.parse("2020-01-01T00:00:00Z"),
        modifyTime = OffsetDateTime.parse("2022-01-01T00:01:00Z"),
    )
}