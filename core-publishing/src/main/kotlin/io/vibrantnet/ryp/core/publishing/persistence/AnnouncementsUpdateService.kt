package io.vibrantnet.ryp.core.publishing.persistence

import com.mongodb.client.result.UpdateResult
import io.vibrantnet.ryp.core.publishing.model.AnnouncementStatus
import io.vibrantnet.ryp.core.publishing.model.Statistics
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AnnouncementsUpdateService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) {

    fun updateAnnouncementStatus(announcementId: String, newStatus: AnnouncementStatus): Mono<UpdateResult> {
        val query = Query.query(Criteria.where("id").`is`(announcementId))
        val update = Update().set("status", newStatus)
        return reactiveMongoTemplate.updateFirst(query, update, Announcement::class.java)
    }

    fun updateAnnouncementStatistics(announcementId: String, newStatistics: Statistics, newStatus: AnnouncementStatus?): Mono<UpdateResult> {
        val query = Query.query(Criteria.where("id").`is`(announcementId))

        return reactiveMongoTemplate.findOne(query, Announcement::class.java)
            .flatMap { announcement ->
                val updatedStatistics = mergeStatistics(announcement.statistics, newStatistics)
                val update = Update().set("statistics", updatedStatistics)
                if (newStatus != null) {
                    update.set("status", newStatus)
                }
                reactiveMongoTemplate.updateFirst(query, update, Announcement::class.java)
            }
    }

}

fun mergeStatistics(existingStatistics: Statistics, newStatistics: Statistics): Statistics {
    return Statistics(
        sent = mergeMaps(existingStatistics.sent, newStatistics.sent),
        uniqueAccounts = (newStatistics.uniqueAccounts ?: 0) + (existingStatistics.uniqueAccounts ?: 0),
        explicitSubscribers = (newStatistics.explicitSubscribers ?: 0) + (existingStatistics.explicitSubscribers ?: 0),
        delivered = mergeMaps(existingStatistics.delivered, newStatistics.delivered),
        failures = mergeMaps(existingStatistics.failures, newStatistics.failures),
        views = mergeMaps(existingStatistics.views, newStatistics.views)
    )
}

private fun mergeMaps(map1: Map<String, Long>, map2: Map<String, Long>): Map<String, Long> {
    val result = map1.toMutableMap()
    for ((key, value) in map2) {
        result[key] = result.getOrDefault(key, 0L) + value
    }
    return result
}
