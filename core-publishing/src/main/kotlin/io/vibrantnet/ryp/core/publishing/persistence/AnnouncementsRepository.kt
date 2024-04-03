package io.vibrantnet.ryp.core.publishing.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AnnouncementsRepository: ReactiveCrudRepository<Announcement, String>