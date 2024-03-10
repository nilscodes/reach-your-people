package io.vibrantnet.ryp.core.publishing.service

import io.vibrantnet.ryp.core.publishing.model.BasicAnnouncementDto

fun interface AnnouncementsApiService {

    /**
     * POST /announcements/{projectId} : Publish new announcement for a specific project
     * Publish an announcement for a project, token policy, SPO, dRep or social media account to all verified holders that have subscribed to updates, without exposing any social media or messaging identifiers to the publisher.
     *
     * @param projectId The numeric ID of a Project (required)
     * @param announcement Can be either a JSON object with the required data or the raw Activity Streams 2.0 Announcement JSON body (required)
     * @return
     * @see AnnouncementsApi#publishAnnouncementForProject
     */
    fun publishAnnouncementForProject(projectId: Long, announcement: BasicAnnouncementDto)
}
