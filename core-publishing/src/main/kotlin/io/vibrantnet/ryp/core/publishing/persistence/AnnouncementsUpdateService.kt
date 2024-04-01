package io.vibrantnet.ryp.core.publishing.persistence

import com.mongodb.client.result.UpdateResult
import io.vibrantnet.ryp.core.publishing.model.AnnouncementStatus
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
}