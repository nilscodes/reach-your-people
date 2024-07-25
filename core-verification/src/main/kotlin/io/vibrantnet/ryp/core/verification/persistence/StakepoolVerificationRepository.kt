package io.vibrantnet.ryp.core.verification.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StakepoolVerificationRepository: ReactiveCrudRepository<StakepoolVerificationDocument, String>