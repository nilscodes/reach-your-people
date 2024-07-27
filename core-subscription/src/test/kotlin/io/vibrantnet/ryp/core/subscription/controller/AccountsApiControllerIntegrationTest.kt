package io.vibrantnet.ryp.core.subscription.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.subscription.model.*
import io.vibrantnet.ryp.core.subscription.service.AccountsApiService
import io.vibrantnet.ryp.core.subscription.service.ProjectsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

private val defaultAccountDto = AccountDto(
    id = 69,
    displayName = "Cantilever Bridge",
    createTime = OffsetDateTime.parse("2018-08-01T03:00:00Z"),
)
private val defaultLinkedExternalAccountDto = LinkedExternalAccountDto(
    id = 420,
    externalAccount = ExternalAccountDto(921, "123", "jeff", "The Real Jeff", OffsetDateTime.parse("2019-01-01T18:00:00Z"), "discord"),
    role = ExternalAccountRole.OWNER,
    linkTime = OffsetDateTime.parse("2019-01-01T19:00:00Z"),
    lastConfirmed = OffsetDateTime.parse("2019-01-01T20:00:00Z"),
    lastTested = OffsetDateTime.parse("2019-01-01T21:00:00Z"),
    settings = setOf(ExternalAccountSetting.DEFAULT_FOR_NOTIFICATIONS)
)

@WebFluxTest(
    controllers = [AccountsApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
internal class AccountsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean fun accountsApiService() = mockk<AccountsApiService>()
        @Bean fun projectsApiService() = mockk<ProjectsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var accountsApiService: AccountsApiService

    @Autowired
    lateinit var projectsApiService: ProjectsApiService

    @Test
    fun `create account works with correct payload and no referral`() {
        every { accountsApiService.createAccount(any(), null) } answers {
            Mono.just(defaultAccountDto)
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-account-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-default-account-response.json")

        webClient.post()
            .uri("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "/accounts/69")
            .expectBody().json(responseJson)
    }

    @Test
    fun `create account works with correct payload and with referral`() {
        every { accountsApiService.createAccount(any(), 33) } answers {
            Mono.just(defaultAccountDto)
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-account-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-default-account-response.json")

        webClient.post()
            .uri("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "/accounts/69")
            .expectBody().json(responseJson)
    }

    @Test
    fun `create account fails validation with good exception body when display name is empty`() {
        val requestJson = loadJsonFromResource("sample-json/test-create-account-request.json")
            .replace("\"Cantilever Bridge\"", "\"\"")

        webClient.post()
            .uri("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `get account succeeds if account with ID is present`() {
        every { accountsApiService.getAccountById(69) } answers {
            Mono.just(defaultAccountDto)
        }

        val responseJson = loadJsonFromResource("sample-json/test-default-account-response.json")

        webClient.get()
            .uri("/accounts/69")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `get account fails with correct exception body when ID is not present`() {
        every { accountsApiService.getAccountById(69) } throws NoSuchElementException("Account not found.")

        webClient.get()
            .uri("/accounts/69")
            .exchange()
            .expectStatus().isNotFound
            .expectBody().json(loadJsonFromResource("sample-json/test-not-found-exception.json"))
    }

    @Test
    fun `get account by provider type and reference ID works`() {
        every { accountsApiService.findAccountByProviderAndReferenceId("discord", "123") } answers {
            Mono.just(defaultAccountDto)
        }

        val responseJson = loadJsonFromResource("sample-json/test-default-account-response.json")

        webClient.get()
            .uri("/accounts/discord/123")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `get linked external accounts works if account with ID is present`() {
        every { accountsApiService.getLinkedExternalAccounts(69) } answers {
            Flux.fromIterable(listOf(defaultLinkedExternalAccountDto))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-linkedexternalaccounts-response.json")

        webClient.get()
            .uri("/accounts/69/externalaccounts")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `get linked external accounts fails with correct exception body when account with ID is not present`() {
        every { accountsApiService.getLinkedExternalAccounts(69) } throws NoSuchElementException("Account not found.")

        webClient.get()
            .uri("/accounts/69/externalaccounts")
            .exchange()
            .expectStatus().isNotFound
            .expectBody().json(loadJsonFromResource("sample-json/test-not-found-exception.json"))
    }

    @Test
    fun `link external account works if both account and external account with IDs are present`() {
        every { accountsApiService.linkExternalAccount(921, 69) } answers {
            Mono.just(defaultLinkedExternalAccountDto)
        }

        val responseJson = loadJsonFromResource("sample-json/test-link-externalaccount-response.json")

        webClient.put()
            .uri("/accounts/69/externalaccounts/921")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `link external account shows correct exception body when already linked`() {
        every { accountsApiService.linkExternalAccount(921, 69) } throws ExternalAccountAlreadyLinkedException("Account 69 already linked to external account 921")

        webClient.put()
            .uri("/accounts/69/externalaccounts/921")
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody().json(loadJsonFromResource("sample-json/test-link-externalaccount-conflict-response.json"))
    }

    @Test
    fun `unlink external account works if both account and external account with IDs are present`() {
        every { accountsApiService.unlinkExternalAccount(69, 921) } just Runs

        webClient.delete()
            .uri("/accounts/69/externalaccounts/921")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Test
    fun `update account works with correct payload`() {
        every { accountsApiService.updateAccountById(69, any()) } answers {
            Mono.just(defaultAccountDto)
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-account-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-default-account-response.json")

        webClient.patch()
            .uri("/accounts/69")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating linked external account works with correct payload`() {
        every { accountsApiService.updateLinkedExternalAccount(69, 921, any()) } answers {
            Mono.just(defaultLinkedExternalAccountDto)
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-linkedexternalaccount-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-linkedexternalaccount-response.json")

        webClient.patch()
            .uri("/accounts/69/externalaccounts/921")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting projects owned by a specific account`() {
        every { projectsApiService.getProjectsForAccount(69) } answers {
            Flux.fromIterable(
                listOf(
                    makeProjectDto(69).copy(
                        registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z")
                    ),
                    makeProjectDto(70).copy(
                        registrationTime = OffsetDateTime.parse("2021-09-02T00:00:00Z")
                    ),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-projects-for-account-response.json")

        webClient.get()
            .uri("/accounts/69/projects")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `subscribing an account to a project works`() {
        every { accountsApiService.subscribeAccountToProject(69, 420, NewSubscriptionDto(status = SubscriptionStatus.SUBSCRIBED)) } answers {
            Mono.just(NewSubscriptionDto(status = SubscriptionStatus.SUBSCRIBED))
        }

        val requestJson = loadJsonFromResource("sample-json/test-subscribe-account-to-project-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-subscribe-account-to-project-response.json")

        webClient.put()
            .uri("/accounts/69/subscriptions/projects/420")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `unsubscribing an account from a project works`() {
        every { accountsApiService.unsubscribeAccountFromProject(69, 420) } answers {
            Mono.empty()
        }

        webClient.delete()
            .uri("/accounts/69/subscriptions/projects/420")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Test
    fun `getting all active subscriptions for an account works`() {
        every { accountsApiService.getAllSubscriptionsForAccount(69) } answers {
            Flux.fromIterable(
                listOf(
                    ProjectSubscriptionDto(69, currentStatus = SubscriptionStatus.BLOCKED),
                    ProjectSubscriptionDto(70, defaultStatus = DefaultSubscriptionStatus.SUBSCRIBED, currentStatus = SubscriptionStatus.DEFAULT),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-all-subscriptions-for-account-response.json")

        webClient.get()
            .uri("/accounts/69/subscriptions")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting account settings works`() {
        every { accountsApiService.getSettingsForAccount(69) } answers {
            Mono.just(
                SettingsDto(
                    setOf(
                        SettingDto("NOTIFICATIONS", "true"),
                        SettingDto("EMAIL", "false"),
                    )
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-settings-for-account-response.json")

        webClient.get()
            .uri("/accounts/69/settings")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating account setting works`() {
        every { accountsApiService.updateAccountSetting(69, "NOTIFICATIONS", SettingDto("NOTIFICATIONS", "false")) } answers {
            Mono.just(SettingDto("NOTIFICATIONS", "false"))
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-account-setting-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-account-setting-response.json")

        webClient.put()
            .uri("/accounts/69/settings/NOTIFICATIONS")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating account settings fails if the body and URL do not match`() {
        every { accountsApiService.updateAccountSetting(69, "notifications", SettingDto("notifications", "false")) } answers {
            Mono.just(SettingDto("notifications", "false"))
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-account-setting-request.json")
            .replace("\"NOTIFICATIONS\"", "\"email\"")

        webClient.put()
            .uri("/accounts/69/settings/NOTIFICATIONS")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `deleting account settings works`() {
        every { accountsApiService.deleteAccountSetting(69, "NOTIFICATIONS") } answers {
            Mono.empty()
        }

        webClient.delete()
            .uri("/accounts/69/settings/NOTIFICATIONS")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Test
    fun `getting the notification settings of an account for a specific project works`() {
        every { accountsApiService.getNotificationsSettingsForAccountAndProject(69, 420) } answers {
            Flux.fromIterable(
                listOf(
                ProjectNotificationSettingDto(
                    772,
                    420,
                    26121,
                    OffsetDateTime.parse("2021-09-01T00:00:00Z")
                ),
                    ProjectNotificationSettingDto(
                        773,
                        420,
                        26122,
                        OffsetDateTime.parse("2021-12-01T00:00:00Z")
                    ),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-notifications-settings-for-account-and-project-response.json")

        webClient.get()
            .uri("/accounts/69/projects/420/notifications")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating the project notifications settings for an account works`() {
        every { accountsApiService.updateNotificationsSettingsForAccountAndProject(69, 420, listOf(
            ProjectNotificationSettingDto(
                772,
                420,
                26121,
            ),
            ProjectNotificationSettingDto(
                773,
                420,
                26122,
            ),
        )) } answers {
            Flux.fromIterable(
                listOf(
                    ProjectNotificationSettingDto(
                        772,
                        420,
                        26121,
                        OffsetDateTime.parse("2021-09-01T00:00:00Z")
                    ),
                    ProjectNotificationSettingDto(
                        773,
                        420,
                        26122,
                        OffsetDateTime.parse("2021-12-01T00:00:00Z")
                    ),
                )
            )
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-notifications-settings-for-account-and-project-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-notifications-settings-for-account-and-project-response.json")

        webClient.put()
            .uri("/accounts/69/projects/420/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}