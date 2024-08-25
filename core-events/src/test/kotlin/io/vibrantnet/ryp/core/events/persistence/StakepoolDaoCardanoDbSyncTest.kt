package io.vibrantnet.ryp.core.events.persistence

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import io.vibrantnet.ryp.core.events.model.StakepoolVoteDetailsDto
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import reactor.test.StepVerifier
import java.sql.ResultSet

internal class StakepoolDaoCardanoDbSyncTest {
    @Test
    fun `test retrieval of retiring stakepools`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val resultMock = mockk<ResultSet>()
        val stakepoolDao = StakepoolDaoCardanoDbSync(jdbcTemplate)

        val retirement = StakepoolRetirementDto(1, "x", 0, "poolHash")

        every { resultMock.getLong("id") } returns retirement.id
        every { resultMock.getString("transactionHash") } returns retirement.transactionHash
        every { resultMock.getInt("index") } returns retirement.transactionIndex
        every { resultMock.getString("poolHash") } returns retirement.poolHash

        every {
            jdbcTemplate.query(any(), any<RowMapper<StakepoolRetirementDto>>(), any<Long>())
        } answers {
            val rowMapper = arg<RowMapper<StakepoolRetirementDto>>(1)
            listOf(resultMock).map { rowMapper.mapRow(it, 0) }
        }

        val result = stakepoolDao.getStakepoolRetirementsWithIdsHigherThan(1)
        StepVerifier.create(result)
            .expectNext(retirement)
            .verifyComplete()
    }

    @Test
    fun `test retrieval of stakepool votes`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val resultMock = mockk<ResultSet>()
        val stakepoolDao = StakepoolDaoCardanoDbSync(jdbcTemplate)

        val vote = StakepoolVoteDetailsDto(1, "x", 0, 1, "poolHash", "anchor")

        every { resultMock.getLong("id") } returns vote.id
        every { resultMock.getString("transactionHash") } returns vote.transactionHash
        every { resultMock.getInt("index") } returns vote.transactionIndex
        every { resultMock.getLong("proposalId") } returns vote.proposalId
        every { resultMock.getString("poolHash") } returns vote.poolHash
        every { resultMock.getString("votingAnchorUrl") } returns vote.votingAnchorUrl

        every {
            jdbcTemplate.query(any(), any<RowMapper<StakepoolVoteDetailsDto>>(), any<Long>())
        } answers {
            val rowMapper = arg<RowMapper<StakepoolVoteDetailsDto>>(1)
            listOf(resultMock).map { rowMapper.mapRow(it, 0) }
        }

        val result = stakepoolDao.getStakepoolVotesWithIdsHigherThan(1)
        StepVerifier.create(result)
            .expectNext(vote)
            .verifyComplete()
    }
}