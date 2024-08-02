package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.points.PointsClaimDto

fun interface PointsClaimService {
    fun sendPointsClaim(pointsClaim: PointsClaimDto)
}