package io.vibrantnet.ryp.core.verification.persistence

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import io.vibrantnet.ryp.core.verification.model.NoCip66DataAvailable
import io.vibrantnet.ryp.core.verification.service.cip66Payload
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import reactor.test.StepVerifier
import java.sql.ResultSet

internal class Cip66DaoCardanoDbSyncTest {
    @Test
    fun `test getCip66Payload parsing`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val resultMock = mockk<ResultSet>()
        val policyId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b"
        val cip66PayloadDto = cip66Payload
        val cip66CardanoDbSyncService = Cip66DaoCardanoDbSync(jdbcTemplate)

        every {
            resultMock.getString("json")
        } returns "{\"version\": \"1.0\", \"0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b\": {\"type\": \"Ed25519VerificationKey2020\", \"files\": [{\"src\": \"ipfs://QmbV8ZcKvPVR5dC2DciQ6kbdfNPoMYkWhXq2FXoxbgDKro\", \"name\": \"CIP-0066_NMKR_IAMX\", \"mediaType\": \"application/ld+json\"}], \"@context\": \"https://github.com/IAMXID/did-method-iamx\"}}"

        every {
            jdbcTemplate.queryForObject(any(), any<RowMapper<Cip66PayloadDto>>(), any<String>(), any<String>(), any<String>())
        } answers {
            val rowMapper = arg<RowMapper<Cip66PayloadDto>>(1)
            rowMapper.mapRow(resultMock, 0)
        }

        val result = cip66CardanoDbSyncService.getCip66Payload(policyId)
        StepVerifier.create(result)
            .expectNext(cip66PayloadDto)
            .verifyComplete()
    }

    @Test
    fun `getCip66Payload should return NoCip66DataAvailable when no data is found`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val cip66CardanoDbSyncService = Cip66DaoCardanoDbSync(jdbcTemplate)

        every {
            jdbcTemplate.queryForObject(any(), any<RowMapper<Cip66PayloadDto>>(), any<String>(), any<String>(), any<String>())
        } throws EmptyResultDataAccessException(1)

        val result = cip66CardanoDbSyncService.getCip66Payload("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectError(NoCip66DataAvailable::class.java)
            .verify()
    }
}