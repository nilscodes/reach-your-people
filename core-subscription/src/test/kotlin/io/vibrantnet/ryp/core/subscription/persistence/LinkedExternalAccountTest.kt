package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.ExternalAccountRole
import io.ryp.shared.model.ExternalAccountSetting
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LinkedExternalAccountTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(LinkedExternalAccount::class.java)
            .withIgnoredFields("linkTime")
            .verify()
    }

    @Test
    fun `test if external account settings get correctly converted from bit string to enum set`() {
        val linkedExternalAccount = LinkedExternalAccount(
            accountId = 1,
            externalAccount = ExternalAccount(referenceId = "test", referenceName = null, displayName = null, type = "test"),
            role = ExternalAccountRole.OWNER,
            settings = "1111111111101001"
        )
        Assertions.assertEquals(
            setOf(
                ExternalAccountSetting.NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.STAKEPOOL_ANNOUNCEMENTS
            ),
            linkedExternalAccount.settingsAsSet()
        )
    }

    @Test
    fun `test if external account settings get correctly converted from enum set to bit string`() {
        val linkedExternalAccount = LinkedExternalAccount(
            accountId = 1,
            externalAccount = ExternalAccount(referenceId = "test", referenceName = null, displayName = null, type = "test"),
            role = ExternalAccountRole.OWNER,
            settings = "0000000000000000"
        )
        linkedExternalAccount.settingsFromSet(
            setOf(
                ExternalAccountSetting.NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.STAKEPOOL_ANNOUNCEMENTS
            )
        )
        Assertions.assertEquals("1111111111101001", linkedExternalAccount.settings)
    }

    @Test
    fun `cannot have more enum entries than the current bit string size`() {
        Assertions.assertTrue(ExternalAccountSetting.entries.size <= 16)
    }

    @Test
    fun `toDto should have required fields`() {
        val linkedExternalAccount = LinkedExternalAccount(
            accountId = 1,
            externalAccount = ExternalAccount(referenceId = "test", referenceName = null, displayName = null, type = "test"),
            role = ExternalAccountRole.OWNER,
            settings = "0000000000000000"
        )
        val linkedExternalAccountDto = linkedExternalAccount.toDto()
        Assertions.assertEquals(linkedExternalAccount.id, linkedExternalAccountDto.id)
        Assertions.assertEquals(linkedExternalAccount.linkTime, linkedExternalAccountDto.linkTime)
        Assertions.assertEquals(linkedExternalAccount.externalAccount.toDto(), linkedExternalAccountDto.externalAccount)
        Assertions.assertEquals(linkedExternalAccount.role, linkedExternalAccountDto.role)
    }
}