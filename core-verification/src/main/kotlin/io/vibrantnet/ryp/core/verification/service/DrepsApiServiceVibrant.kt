package io.vibrantnet.ryp.core.verification.service

import io.vibrantnet.ryp.core.verification.persistence.DrepDao
import org.springframework.stereotype.Service

@Service
class DrepsApiServiceVibrant(
    private val drepDao: DrepDao,
) : DrepsApiService {
    override fun getDRepDetails(drepId: String) = drepDao.getDrepDetails(drepId)
}