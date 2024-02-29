---
status: completed
date: 2024-02-27
---
# 3️⃣ Project Authentication and Verification

## 💡 Purpose
This document outlines the goals and outcomes of Milestone 3 of the Catalyst Fund 10 project "Reach your people - social media and messaging integrations for DAOs and NFT projects" (further referenced to as RYP).

It starts with the project ID, project and milestone links, the outputs, acceptance criteria and proposed evidence for the Proof of Achievement in the milestone module. After that follows a results section that includes the evidence, links, explanations, and supporting documentation.

## 🆔 Project ID
`1000156`

## 🔗 Important Links

- Catalyst: <https://projectcatalyst.io/funds/10/f10-daos-less3-cardano/reach-your-people-social-media-and-messaging-integrations-for-daos-and-nft-projects>
- Milestone Module: <https://milestones.projectcatalyst.io/projects/1000156/milestones/3>
- Ideascale: <https://cardano.ideascale.com/c/idea/106255>

## 🧱 Milestone Outputs
- Metadata model and samples documented via ADR and if needed via OpenAPI definitions
- First integration (i.e. service that provides messaging to end user) is documented via ADR
- Sample website that allows wallet integration AND the ability to provide either connection information or an authentication flow with integration service exists

## ✔ Acceptance criteria
- The model that is used to describe activities/postings that projects submit to the service etc. is sufficiently described and samples provided - this may include ADRs and improved OpenAPI definitions (this is mostly a research and documentation task)
- A decision was made on which service will provide the first broadcast messages (Discord, Twitter, Text messages etc.) Note: A service might be in development but no running prototype that actually sends out notifications is required at this time. Instead, a sample website should exist that showcases connectivity (authentication flow via OAuth for a social media service, or the ability to provide a phone number) that will allow connecting the user and their info with at least one wallet. The sample website should not use persistence or store user data long term.

## 🧾 Evidence of milestone completion	
- URL to working sample website is provided
- Code for sample website is available in the main repository or an adjacent public source repository
- All other evidence will be provided either as content of the public repository or as links that are available from the public repository

## 🚀 Results
The following sections provide all documentation and context and detailed links for each output where applicable:

### Sample Website
The existing sample website from milestone 2 was extended to include social media login capabilities and wallet connection capabilities, as well as linking/unlinking accounts. It does not yet use persistent storage (so if you test, it could be gone the next day), as documented in the AC.

A video was recorded to better explain the capabilities matching the current status and required evidence for this milestone: <https://youtu.be/BBYawlh6_ag>

The website code is available in the main repository at [/example/demo-site](../../example/demo-site).

The live website is at https://ryp.vibrantnet.io

### Messaging Service Decision
The messaging service decision was made previously already and is documented here, including alternatives that were investigated as well as pros and cons: [0002-first-messaging-integration-discord.md](../decisions/0002-first-messaging-integration-discord.md)

### Metadata Model
The decision on which metadata model will be used for publishing was the main focus of research for the past weeks and is sufficiently documented in ADR-0005: [0005-announcement-metadata.md](../decisions/0005-announcement-metadata.md)

The ADR contains sample announcements for two use cases, and in addition, the API documentation for the publishing service was augmented with the JSON structure that will be required for submitting announcements: [core-publishing-service.yaml](../api/core-publishing-service.yaml)

The end users will likely not have to build these structures themselves, unless they publish from an external source via REST API, and instead will be able to use a UI to build out their announcement for the various media and use cases.