package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.BasicAnnouncementDto
import io.ryp.shared.model.BasicAnnouncementWithIdDto
import io.vibrantnet.ryp.core.publishing.model.AnnouncementDto
import io.vibrantnet.ryp.core.publishing.model.PublishingPermissionsDto
import io.vibrantnet.ryp.core.publishing.persistence.Announcement
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AnnouncementsApiService {
    fun createAnnouncement(announcement: BasicAnnouncementWithIdDto, projectId: Long): Mono<Announcement>

    /**
     * POST /announcements/{projectId} : Publish new announcement for a specific project
     * Publish an announcement for a project, token policy, SPO, dRep or social media account to all verified holders that have subscribed to updates, without exposing any social media or messaging identifiers to the publisher.
     *
     * @param projectId The numeric ID of a Project (required)
     * @param announcement Can be either a JSON object with the required data or the raw Activity Streams 2.0 Announcement JSON body (required)
     * @return
     * @see AnnouncementsApi#publishAnnouncementForProject
     */
    fun publishAnnouncementForProject(projectId: Long, announcement: BasicAnnouncementDto): Mono<AnnouncementDto>

    /**
     * GET /projects/{projectId}/announcements : List announcements for a specific project
     * List all announcements that a specific project has published, regardless of target.
     *
     * @param projectId The numeric ID of a Project (required)
     * @return OK (status code 200)
     * @see ProjectsApi#listAnnouncementsForProject
     */
    fun listAnnouncementsForProject(projectId: Long): Flux<AnnouncementDto>

    /**
     * GET /projects/{projectId}/roles/{accountId} : Get the publishing role status
     * Get the roles and permissions to publishing rights for a project and the related policies and assets.
     *
     * @param projectId The numeric ID of a Project (required)
     * @param accountId The numeric ID of an account (required)
     * @return OK (status code 200)
     * @see ProjectsApi#getPublishingPermissionsForAccount
     */
    fun getPublishingPermissionsForAccount(projectId: Long, accountId: Long): Mono<PublishingPermissionsDto>

    /**
     * GET /announcements/{announcementId} : Get announcement by ID
     *
     * @param announcementId The UUID of an announcement (required)
     * @return
     * @see AnnouncementsApi#getAnnouncementById
     */
    fun getAnnouncementById(announcementId: UUID): Mono<AnnouncementDto>
}
