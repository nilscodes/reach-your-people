package io.vibrantnet.ryp.core.events.persistence

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import reactor.test.StepVerifier
import java.sql.ResultSet

internal class DrepVoteDaoCardanoDbSyncTest {
    @Test
    fun `test retrieval of dRep votes`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val resultMock = mockk<ResultSet>()
        val drepVoteDao = DrepVoteDaoCardanoDbSync(jdbcTemplate)

        val vote = DRepVoteDetailsDto(1, "x", 0, 1, "drep", "anchor")

        every { resultMock.getLong("id") } returns vote.id
        every { resultMock.getString("transactionHash") } returns vote.transactionHash
        every { resultMock.getInt("index") } returns vote.transactionIndex
        every { resultMock.getLong("proposalId") } returns vote.proposalId
        every { resultMock.getString("drepId") } returns vote.drepId
        every { resultMock.getString("votingAnchorUrl") } returns vote.votingAnchorUrl

        every {
            jdbcTemplate.query(any(), any<RowMapper<DRepVoteDetailsDto>>(), any<Long>())
        } answers {
            val rowMapper = arg<RowMapper<DRepVoteDetailsDto>>(1)
            listOf(resultMock).map { rowMapper.mapRow(it, 0) }
        }

        val result = drepVoteDao.getDrepVotesWithIdsHigherThan(1)
        StepVerifier.create(result)
            .expectNext(vote)
            .verifyComplete()
    }
}