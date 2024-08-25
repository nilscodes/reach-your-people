package io.ryp.cardano.model.governance

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GovernanceActionTypeTest {
    @Test
    fun `cardano-db-sync versions of GovernanceActionType should be mapped correctly`() {
        assertEquals(GovernanceActionType.MOTION_NO_CONFIDENCE, GovernanceActionType.fromString("NoConfidence"))
        assertEquals(GovernanceActionType.COMMITTEE_UPDATE, GovernanceActionType.fromString("NewCommittee"))
        assertEquals(GovernanceActionType.CONSTITUTION_UPDATE, GovernanceActionType.fromString("NewConstitution"))
        assertEquals(GovernanceActionType.HARD_FORK_INITIATION, GovernanceActionType.fromString("HardForkInitiation"))
        assertEquals(GovernanceActionType.PROTOCOL_PARAMETER_CHANGE, GovernanceActionType.fromString("ParameterChange"))
        assertEquals(GovernanceActionType.TREASURY_WITHDRAWALS, GovernanceActionType.fromString("TreasuryWithdrawals"))
        assertEquals(GovernanceActionType.INFO, GovernanceActionType.fromString("InfoAction"))
    }

    @Test
    fun `unmatched cardano-db-sync types map to UNKNOWN`() {
        assertEquals(GovernanceActionType.UNKNOWN, GovernanceActionType.fromString("Potatoes"))
    }
}