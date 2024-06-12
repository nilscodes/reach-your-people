package io.vibrantnet.ryp.core.points.persistence

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PointsClaimRepository: CrudRepository<PointsClaim, String> {
    fun findAllByAccountId(accountId: Long): List<PointsClaim>
    fun findAllByAccountIdAndTokenId(accountId: Long, tokenId: Int): List<PointsClaim>

    @Query("SELECT p.tokenId AS tokenId, COALESCE(SUM(p.points), 0) AS points FROM PointsClaim p WHERE p.claimed = true AND p.points > 0 AND p.accountId = :accountId GROUP BY p.tokenId")
    fun getTotalPointsClaimedByTokenIdAndAccountId(accountId: Long): List<PointsByTokenProjection>

    @Query("SELECT p.tokenId AS tokenId, COALESCE(SUM(p.points), 0) AS points FROM PointsClaim p WHERE p.claimed = true AND p.points < 0 AND p.accountId = :accountId GROUP BY p.tokenId")
    fun getTotalPointsSpentByTokenIdAndAccountId(accountId: Long): List<PointsByTokenProjection>

    @Query("SELECT p.tokenId AS tokenId, COALESCE(SUM(p.points), 0) AS points FROM PointsClaim p WHERE p.claimed = false AND p.accountId = :accountId GROUP BY p.tokenId")
    fun getTotalPointsClaimableByTokenIdAndAccountId(accountId: Long): List<PointsByTokenProjection>

    @Query("SELECT p.tokenId AS tokenId, COALESCE(SUM(CASE WHEN p.claimed = true AND p.points > 0 THEN p.points ELSE 0 END), 0) + COALESCE(SUM(CASE WHEN p.claimed = true AND p.points < 0 THEN p.points ELSE 0 END), 0) AS points FROM PointsClaim p WHERE p.accountId = :accountId GROUP BY p.tokenId")
    fun getTotalPointsAvailableByTokenIdAndAccountId(accountId: Long): List<PointsByTokenProjection>

}