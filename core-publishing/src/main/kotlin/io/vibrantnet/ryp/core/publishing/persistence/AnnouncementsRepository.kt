package io.vibrantnet.ryp.core.publishing.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface AnnouncementsRepository: ReactiveCrudRepository<Announcement, String> {
    fun findByProjectId(projectId: Long): Flux<Announcement>
}