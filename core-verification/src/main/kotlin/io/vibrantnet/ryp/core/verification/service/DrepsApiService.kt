package io.vibrantnet.ryp.core.verification.service

import io.ryp.cardano.model.DRepDetailsDto
import reactor.core.publisher.Mono

fun interface DrepsApiService {

    /**
     * GET /dreps/{drepId} : Get dRep details
     *
     * @param drepId The dRep ID of a dRep (required)
     * @return The dRep details (status code 200)
     * @see DrepsApi#getDRepDetails
     */
    fun getDRepDetails(drepId: String): Mono<DRepDetailsDto>
}
