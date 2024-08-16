package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

/**
 *
 * @param drepId The dRep pubkey blake hash in hex
 * @param drepView The dRep ID of a dRep, in viewable Bech32 format
 * @param displayName
 * @param currentEpoch Epoch when the collected information applies
 * @param delegation Delegation in lovelace, at the epoch boundary matching the epoch property
 * @param activeUntil Epoch until currently marked as active, if available - considered inactive if empty (either newly registered or no longer active).
 */
data class DRepDetailsDto(
    @Pattern(regexp = "^[A-Fa-f0-9]{56}$")
    @JsonProperty("drepId", required = true)
    val drepId: String,

    @Pattern(regexp = "^drep1[a-zA-Z0-9]{51}$")
    @JsonProperty("drepView", required = true)
    val drepView: String,

    @JsonProperty("displayName", required = true)
    val displayName: String,

    @JsonProperty("currentEpoch", required = true)
    val currentEpoch: Int,

    @field:Min(0)
    @JsonProperty("delegation", required = true)
    val delegation: Long,

    @JsonProperty("activeUntil")
    val activeUntil: Int? = null
) {

}

