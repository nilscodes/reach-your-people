package io.vibrantnet.ryp.core.publishing.service

import io.vibrantnet.ryp.core.publishing.model.BasicAnnouncementDto
import org.springframework.stereotype.Service

@Service
class AnnouncementsApiServiceVibrant: AnnouncementsApiService {
    override fun publishAnnouncementForProject(
        projectId: Long,
        announcement: BasicAnnouncementDto
    ) {
        // TODO verify that the user has the right to publish announcements for the project via verification service
        println("Publishing announcement for project $projectId")
        println(announcement)
        // TODO Get all subscriptions and associated publishing channels
        // TODO publish announcement to queue
    }
}