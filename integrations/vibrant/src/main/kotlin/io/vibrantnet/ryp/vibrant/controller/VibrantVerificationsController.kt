package io.vibrantnet.ryp.vibrant.controller

import io.vibrantnet.ryp.vibrant.service.VibrantCommunityService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class VibrantVerificationsController(
    private val vibrantCommunityService: VibrantCommunityService,
) {

    @GetMapping("/externalaccounts/discord/{discordUserId}/verifications")
    fun getVerificationsForDiscordUserId(@PathVariable discordUserId: Long) =
        vibrantCommunityService.getVerificationsForDiscordUserId(discordUserId)
}
