package io.vibrantnet.ryp.core.publishing.persistence

import io.vibrantnet.ryp.core.publishing.model.ActivityStream
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AnnouncementsRepository: ReactiveCrudRepository<Announcement, String>