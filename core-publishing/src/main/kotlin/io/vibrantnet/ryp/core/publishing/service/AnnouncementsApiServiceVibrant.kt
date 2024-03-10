package io.vibrantnet.ryp.core.publishing.service

import io.vibrantnet.ryp.core.publishing.model.BasicAnnouncementDto
import io.vibrantnet.ryp.core.publishing.model.UserNotAuthorizedToPublishException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AnnouncementsApiServiceVibrant(
    val subscriptionService: SubscriptionService,
    val verifyService: VerifyService,
) : AnnouncementsApiService {

    override fun publishAnnouncementForProject(
        projectId: Long,
        announcement: BasicAnnouncementDto
    ): Mono<Unit> {
        val linkedAccountsForAuthor = subscriptionService.getLinkedExternalAccounts(announcement.author)
        return subscriptionService.getProject(projectId)
            .flatMap { project ->
                // For each policy in the project, check if any of them are CIP-66 and verify the user's right to publish announcements
                Flux.fromIterable(project.policies)
                    .flatMap { policy ->
                        linkedAccountsForAuthor.flatMap {linkedAccount ->
                            verifyService.verifyCip66(policy.policyId, linkedAccount.externalAccount.type, linkedAccount.externalAccount.referenceId)
                        }
                    }
                    .any { it } // If any of the verifications succeeded
                    .flatMap { verified ->
                        if (!verified) {
                            Mono.error(UserNotAuthorizedToPublishException("User with account ID ${announcement.author} is not authorized to publish announcements for project $projectId"))
                        } else {
                            println("Publishing announcement for project $projectId")
                            println(announcement)
                            // TODO Get all subscriptions and associated publishing channels
                            // TODO publish announcement to queue
                            Mono.empty()
                        }
                    }
            }
    }
}