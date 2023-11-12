---
status: proposed
date: 2023-11-11
deciders: Nils Codes
---
# The first messaging integration will be Discord

## Context and Problem Statement

To make progress and prove the feasibility of the overall architecture and solution, an existing instant messaging or social media application needs to be chosen to build the first integration for. It should have a large amount of adoption (1000+ users) within the existing Cardano ecosystem, to allow early rollout and beta testing. The application also needs to have good enough documentation to allow building an integration for.

## Decision Drivers

* Proven user-base on Cardano
* 1000+ users
* Easy to integrate
* Easy to verify ownership
* Cost

## Considered Options

* Twitter
* Discord
* E-mail (via a service like [Mailgun](https://mailgun.com) or [Sendgrid](https://sendgrid.com))
* Text Messages (via a service like [Twilio](https://twilio.com))
* Telegram
* Slack
* MS Teams

## Decision Outcome

Discord was chosen because it fits all criteria. Discord has a large representation within the Cardano community (within the last two years, at least 40,000 users have interacted with the [HAZELnet](https://www.vibrantnet.io) bot), as can be seen on the [public Grafana dashboard](https://hazelpool.grafana.net/public-dashboards/37b024d96570451780c6354f70944b47). Almost all NFT projects and many stakepools have Discord servers and interact with their community via Discord. Their [API documentation](https://discord.com/developers/docs/intro) and general support for libraries that allow interacting with the service are readily available ([DiscordJS](https://discord.js.org)).

Verifying ownership of a Discord account is a simple OAuth flow and already used by a variety of projects on Cardano ([CNFT Tools](https://cnft.tools), [Vibrant](https://www.vibrantnet.io), [Skulliance](https://skulliance.io), [Tavern Squad](https://tavernsquad.io) and [Derp Birds](https://derpbirds.io) for example). As such, these projects would have an easy way to integrate with the subscription or verification modules of the planned architecture.

A Discord integration is free from ongoing costs, as opposed to some of the more general-purpose approaches like e-mail and text messages, and there are currently no known cost requirements for using their APIs (unlike Twitter).

The downside of choosing Discord is the initial exclusion of people that do not use it. However, since there are plans to add additional integrations as part of the project roadmap, the impact should be minor, as the first integration is primarily for validating the service itself and alpha testing.

Slack and MS Teams, while accepted in the enterprise setting, do not have enough adoption within the Cardano Ecosystem. Telegram has adoption, however is used by less projects, and as such is a good candidate for a second or third integration.

## More Information

There is an existing open-source Discord integration that can be leveraged to develop faster, as it is maintained by the same team that is building the "Reach Your People" app: [Vibrant](https://www.github.com/nilscodes/hazelnet)