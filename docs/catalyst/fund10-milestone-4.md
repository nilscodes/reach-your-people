---
status: completed
date: 2024-03-14
---
# 4️⃣ Finalize Initial Integration	

## 💡 Purpose
This document outlines the goals and outcomes of Milestone 4 of the Catalyst Fund 10 project "Reach your people - social media and messaging integrations for DAOs and NFT projects" (further referenced to as RYP).

It starts with the project ID, project and milestone links, the outputs, acceptance criteria and proposed evidence for the Proof of Achievement in the milestone module. After that follows a results section that includes the evidence, links, explanations, and supporting documentation.

## 🆔 Project ID
`1000156`

## 🔗 Important Links

- Catalyst: <https://projectcatalyst.io/funds/10/f10-daos-less3-cardano/reach-your-people-social-media-and-messaging-integrations-for-daos-and-nft-projects>
- Milestone Module: <https://milestones.projectcatalyst.io/projects/1000156/milestones/4>
- Ideascale: <https://cardano.ideascale.com/c/idea/106255>

## 🧱 Milestone Outputs
- A video showcasing a user connecting their wallet and integration information, followed by an authentication flow for a project, and the project sending an announcement as well as the user receiving it on their expected channel exists

## ✔ Acceptance criteria
- At this point the project should have fully working prototype that can be showcased

## 🧾 Evidence of milestone completion	
- Video is provided via private YouTube link
- All other evidence will be provided either as content of the public repository or as links that are available from the public repository

## 🚀 Results
In Milestone 4, a lot of capabilities and UI were added to the project. Primarily, a complete user interface and flow for both project and subscription management as well as wallet configuration was added. The backend services were enhanced with ancillary services like Redis for caching and RabbitMQ as the core queueing solution documented in milestone 1.

In addition, all three services were actively developed to be able to query individual and project-wide wallet information, publish to a queue for each integration based on subscription information, support explicit subscription and blocking, add projects and verify announcements can only be sent by verified [CIP-0066](https://github.com/cardano-foundation/CIPs/pull/294) identities. In addition the first integration service was created and sends verified announcements to valid Discord subscribers.

The frontend and what it means to use these capabilities in the role of a publisher and subscriber is outlined in this 12 minute User Journey Video:

<https://www.youtube.com/watch?v=BXoaME4KzvY>

All relevant code mentioned above has been pushed to the repository at <https://gitlab.com/vibrantnet/ryp>.

Instructions on how to build the tool and use it locally have been added and the demo in the video was performed live on <https://ryp.vibrantnet.io>.

Persistence is still not implemented for the subscription service, so any subscriptions, registered accounts or wallets disappear when the service is restarted. This is intentional to simplify testing and resetting and will change for milestone 5.