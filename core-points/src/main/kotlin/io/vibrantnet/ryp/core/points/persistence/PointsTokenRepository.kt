package io.vibrantnet.ryp.core.points.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PointsTokenRepository: CrudRepository<PointsToken, Int>