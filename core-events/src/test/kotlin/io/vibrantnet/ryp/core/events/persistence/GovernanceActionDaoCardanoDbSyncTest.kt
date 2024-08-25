package io.vibrantnet.ryp.core.events.persistence

import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.governance.GovernanceActionType
import io.vibrantnet.ryp.core.events.model.GovernanceActionProposalDto
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import reactor.test.StepVerifier
import java.sql.ResultSet

internal class GovernanceActionDaoCardanoDbSyncTest {
    @Test
    fun `test retrieval of new governance action proposals`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val resultMock = mockk<ResultSet>()
        val governanceActionDao = GovernanceActionDaoCardanoDbSync(jdbcTemplate)

        val proposal = GovernanceActionProposalDto(1, "x", 0, GovernanceActionType.PROTOCOL_PARAMETER_CHANGE, "anchor")

        every { resultMock.getLong("proposalId") } returns proposal.proposalId
        every { resultMock.getString("transactionHash") } returns proposal.transactionHash
        every { resultMock.getInt("index") } returns proposal.transactionIndex
        every { resultMock.getString("type") } returns "ParameterChange"
        every { resultMock.getString("votingAnchorUrl") } returns proposal.votingAnchorUrl

        every {
            jdbcTemplate.query(any(), any<RowMapper<GovernanceActionProposalDto>>(), any<Long>())
        } answers {
            val rowMapper = arg<RowMapper<GovernanceActionProposalDto>>(1)
            listOf(resultMock).map { rowMapper.mapRow(it, 0) }
        }

        val result = governanceActionDao.getGovernanceActionsWithIdsHigherThan(1)
        StepVerifier.create(result)
            .expectNext(proposal)
            .verifyComplete()
    }
}