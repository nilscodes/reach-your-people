package io.vibrantnet.ryp.core.points.persistence

interface PointsByTokenProjection {
    val tokenId: Long
    val points: Long
}