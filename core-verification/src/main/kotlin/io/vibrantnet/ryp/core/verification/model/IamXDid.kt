package io.vibrantnet.ryp.core.verification.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class IamXDid @JsonCreator constructor(
    @JsonProperty("payload") val payload: IamXDidPayload
) {
    fun isValidMatch(serviceName: String, referenceId: String): Boolean {
        // TODO Here can enforce different levels of security (verify policy id matches, verify signature of the payload, etc)
        // For now, just check if the referenceId is in the payload
        return /* policyId == this.payload.policyId && */ payload.accounts.any { account ->
            when (account) {
                is TwitterAccount -> serviceName == "twitter" && account.twitterId == referenceId
                is DiscordAccount -> serviceName == "discord" && account.discordId == referenceId
                is SteamAccount -> serviceName == "steam" && account.displayName == referenceId
                is GoogleAccount -> serviceName == "google" && account.googleId == referenceId
                is LinkedInAccount -> serviceName == "linkedin" && account.linkedinId == referenceId
                is AppleAccount -> serviceName == "apple" && account.appleId == referenceId
                is TwitchAccount -> serviceName == "twitch" && account.twitchId == referenceId
                is GitHubAccount -> serviceName == "github" && account.githubId == referenceId
                else -> false
            }
        }
    }
}

data class IamXDidPayload @JsonCreator constructor(
    @JsonProperty("date") val date: OffsetDateTime,
    @JsonProperty("policyID") @JsonDeserialize(using = PolicyIdDeserializer::class) val policyId: String?,
    @JsonProperty("accounts") @JsonDeserialize(using = AccountsDeserializer::class) val accounts: List<Account>,
    @JsonProperty("version") val version: String,
)

sealed class Account

data class TwitterAccount @JsonCreator constructor(
    @JsonProperty("twitterId") val twitterId: String,
    @JsonProperty("username") val username: String
) : Account()

data class DiscordAccount @JsonCreator constructor(
    @JsonProperty("discordId") val discordId: String,
    @JsonProperty("username") val username: String
) : Account()

data class SteamAccount @JsonCreator constructor(
    @JsonProperty("displayName") val displayName: String
) : Account()

data class GoogleAccount @JsonCreator constructor(
    @JsonProperty("googleId") val googleId: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("displayName") val displayName: String
) : Account()

data class LinkedInAccount @JsonCreator constructor(
    @JsonProperty("linkedinId") val linkedinId: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("displayName") val displayName: String
) : Account()

data class AppleAccount @JsonCreator constructor(
    @JsonProperty("appleId") val appleId: String
) : Account()

data class TwitchAccount @JsonCreator constructor(
    @JsonProperty("twitchId") val twitchId: String
) : Account()

data class GitHubAccount @JsonCreator constructor(
    @JsonProperty("githubId") val githubId: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("displayName") val displayName: String
) : Account()

data class UnknownAccount @JsonCreator constructor(
    val data: Map<String, Any>
) : Account()

inline fun <reified T> ObjectMapper.treeToValueTyped(node: JsonNode): T = this.treeToValue(node, T::class.java)

class AccountsDeserializer : JsonDeserializer<List<Account>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<Account> {
        val node: JsonNode = p.codec.readTree(p)
        val accounts = mutableListOf<Account>()
        val objectMapper = p.codec as ObjectMapper

        var idx = 0;
        while (idx < node.size()) {
            val accountType = node[idx].asText()
            val accountData = node[idx + 1]

            val account = when (accountType) {
                "twitter" -> objectMapper.treeToValue(accountData, TwitterAccount::class.java)
                "discord" -> objectMapper.treeToValue(accountData, DiscordAccount::class.java)
                "google" -> objectMapper.treeToValue(accountData, GoogleAccount::class.java)
                "steam" -> objectMapper.treeToValue(accountData, SteamAccount::class.java)
                "linkedin" -> objectMapper.treeToValue(accountData, LinkedInAccount::class.java)
                "apple" -> objectMapper.treeToValue(accountData, AppleAccount::class.java)
                "twitch" -> objectMapper.treeToValue(accountData, TwitchAccount::class.java)
                "github" -> objectMapper.treeToValue(accountData, GitHubAccount::class.java)
                else -> UnknownAccount(objectMapper.treeToValueTyped<Map<String, Any>>(accountData))
            }

            accounts.add(account)
            idx += 2
        }

        return accounts
    }
}

class PolicyIdDeserializer : JsonDeserializer<String?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String? {
        val node: JsonNode = p.codec.readTree(p)
        return when {
            node.isTextual -> node.asText()
            else -> null
        }
    }
}