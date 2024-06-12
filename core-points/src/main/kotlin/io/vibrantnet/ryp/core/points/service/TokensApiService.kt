package io.vibrantnet.ryp.core.points.service

import io.vibrantnet.ryp.core.points.model.PointsTokenDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TokensApiService {

    /**
     * POST /tokens : Create new points token
     * Create a new points token for use in the system
     *
     * @param pointsTokenDto  (optional)
     * @return Created (status code 201)
     * @see TokensApi#createPointsToken
     */
    fun createPointsToken(pointsTokenDto: PointsTokenDto): Mono<PointsTokenDto>

    /**
     * GET /tokens/{tokenId} : Get points token details
     * Get detailed information on a points token
     *
     * @param tokenId The numeric ID of the token (required)
     * @return OK (status code 200)
     * @see TokensApi#getPointsToken
     */
    fun getPointsToken(tokenId: Int): Mono<PointsTokenDto>

    /**
     * GET /tokens : List all points tokens
     * Get a list of all points tokens, without limiting it to specific projects
     *
     * @return OK (status code 200)
     * @see TokensApi#listPointsTokens
     */
    fun listPointsTokens(): Flux<PointsTokenDto>
}
