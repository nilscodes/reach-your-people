package io.vibrantnet.ryp.core.subscription.controller

import io.ryp.shared.model.LinkedExternalAccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.AccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.NewSubscriptionDto
import io.vibrantnet.ryp.core.subscription.model.SettingDto
import io.vibrantnet.ryp.core.subscription.service.AccountsApiService
import io.vibrantnet.ryp.core.subscription.service.ProjectsApiService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class AccountsApiController(
    val accountService: AccountsApiService,
    val projectsService: ProjectsApiService,
) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/accounts"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @Valid @RequestBody accountDto: AccountDto,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<AccountDto>> {
        return accountService.createAccount(accountDto)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let { UriComponentsBuilder.fromUri(it).path("/{id}").buildAndExpand(savedEntity.id).toUri() })
                    .body(savedEntity)
            }
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAccountById(@PathVariable("accountId") accountId: Long) = accountService.getAccountById(accountId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{providerType}/{referenceId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun findAccountByProviderAndReferenceId(
        @PathVariable("providerType") providerType: String,
        @PathVariable("referenceId") referenceId: String) = accountService.findAccountByProviderAndReferenceId(providerType, referenceId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}/externalaccounts"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getLinkedExternalAccounts(@PathVariable("accountId") accountId: Long) = accountService.getLinkedExternalAccounts(accountId)

    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/accounts/{accountId}/externalaccounts/{externalAccountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun linkExternalAccount(
        @PathVariable("externalAccountId") externalAccountId: Long,
        @PathVariable("accountId") accountId: Long
    ) = accountService.linkExternalAccount(externalAccountId, accountId)

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/accounts/{accountId}/externalaccounts/{externalAccountId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateLinkedExternalAccount(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("externalAccountId") externalAccountId: Long,
        @Valid @RequestBody linkedExternalAccountPartial: LinkedExternalAccountPartialDto
    ) = accountService.updateLinkedExternalAccount(accountId, externalAccountId, linkedExternalAccountPartial)

    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/accounts/{accountId}/externalaccounts/{externalAccountId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unlinkExternalAccount(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("externalAccountId") externalAccountId: Long
    ) = accountService.unlinkExternalAccount(accountId, externalAccountId)

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/accounts/{accountId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun updateAccountById(
        @PathVariable("accountId") accountId: Long,
        @Valid @RequestBody accountPartialDto: AccountPartialDto
    ) = accountService.updateAccountById(accountId, accountPartialDto)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}/projects"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getProjectsForAccount(@PathVariable("accountId") accountId: Long) = projectsService.getProjectsForAccount(accountId)

    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/accounts/{accountId}/subscriptions/projects/{projectId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun subscribeAccountToProject(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("projectId") projectId: Long,
        @Valid @RequestBody subscribeAccountToProjectRequest: NewSubscriptionDto,
    ) = accountService.subscribeAccountToProject(accountId, projectId, subscribeAccountToProjectRequest)

    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/accounts/{accountId}/subscriptions/projects/{projectId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unsubscribeAccountFromProject(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("projectId") projectId: Long,
    ) = accountService.unsubscribeAccountFromProject(accountId, projectId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}/subscriptions"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAllSubscriptionsForAccount(@PathVariable("accountId") accountId: Long) = accountService.getAllSubscriptionsForAccount(accountId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/accounts/{accountId}/settings"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getSettingsForAccount(@PathVariable("accountId") accountId: Long) = accountService.getSettingsForAccount(accountId)

    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/accounts/{accountId}/settings/{settingName}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun updateAccountSetting(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("settingName") settingName: String,
        @Valid @RequestBody setting: SettingDto,
    ): Mono<SettingDto> {
        require(setting.name == settingName) { "Account setting name in path $settingName did not match setting in request body ${setting.name}." }
        return accountService.updateAccountSetting(accountId, settingName, setting)
    }

    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/accounts/{accountId}/settings/{settingName}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccountSetting(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("settingName") settingName: String,
    ) = accountService.deleteAccountSetting(accountId, settingName)

}
