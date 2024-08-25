package io.ryp.cardano.model.governance

enum class GovernanceActionType {
    MOTION_NO_CONFIDENCE,
    COMMITTEE_UPDATE,
    CONSTITUTION_UPDATE,
    HARD_FORK_INITIATION,
    PROTOCOL_PARAMETER_CHANGE,
    TREASURY_WITHDRAWALS,
    INFO,
    UNKNOWN;

    companion object {
        fun fromString(value: String): GovernanceActionType {
            return when (value) {
                "NoConfidence" -> MOTION_NO_CONFIDENCE
                "NewCommittee" -> COMMITTEE_UPDATE
                "NewConstitution" -> CONSTITUTION_UPDATE
                "HardForkInitiation" -> HARD_FORK_INITIATION
                "ParameterChange" -> PROTOCOL_PARAMETER_CHANGE
                "TreasuryWithdrawals" -> TREASURY_WITHDRAWALS
                "InfoAction" -> INFO
                else -> UNKNOWN
            }
        }
    }
}


