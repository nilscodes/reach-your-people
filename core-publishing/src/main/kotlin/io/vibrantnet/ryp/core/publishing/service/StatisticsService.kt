package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.StatisticsUpdateDto

fun interface StatisticsService {
    fun processStatisticsUpdate(type: String, statisticsUpdateDto: StatisticsUpdateDto)
}