package io.vibrantnet.ryp.core.points.service

import io.vibrantnet.ryp.core.points.model.PointsTokenDto
import io.vibrantnet.ryp.core.points.persistence.PointsToken
import io.vibrantnet.ryp.core.points.persistence.PointsTokenRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class TokensApiServiceVibrant(
    private val pointsTokenRepository: PointsTokenRepository,
) : TokensApiService {
    override fun listPointsTokens() = Flux.fromIterable(pointsTokenRepository.findAll().map { it.toDto() })

    override fun getPointsToken(tokenId: Int): Mono<PointsTokenDto> {
        val pointsToken = pointsTokenRepository.findById(tokenId).get()
        return Mono.just(pointsToken.toDto())
    }

    override fun createPointsToken(pointsTokenDto: PointsTokenDto): Mono<PointsTokenDto> {
        val pointsToken = PointsToken(
            name = pointsTokenDto.name,
            displayName = pointsTokenDto.displayName,
            creator = pointsTokenDto.creator,
            projectId = pointsTokenDto.projectId,
            createTime = OffsetDateTime.now(),
            modifyTime = OffsetDateTime.now(),
        )
        return Mono.just(pointsTokenRepository.save(pointsToken).toDto())
    }
}