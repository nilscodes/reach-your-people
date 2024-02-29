---
status: completed
date: 2024-01-15
---
# 2️⃣ Prototyping Indexing and Core Services	

## 💡 Purpose
This document outlines the goals and outcomes of Milestone 2 of the Catalyst Fund 10 project "Reach your people - social media and messaging integrations for DAOs and NFT projects" (further referenced to as RYP).

It starts with the project ID, project and milestone links, the outputs, acceptance criteria and proposed evidence for the Proof of Achievement in the milestone module. After that follows a results section that includes the evidence, links, explanations, and supporting documentation.

## 🆔 Project ID
`1000156`

## 🔗 Important Links

- Catalyst: <https://projectcatalyst.io/funds/10/f10-daos-less3-cardano/reach-your-people-social-media-and-messaging-integrations-for-daos-and-nft-projects>
- Milestone Module: <https://milestones.projectcatalyst.io/projects/1000156/milestones/2>
- Ideascale: <https://cardano.ideascale.com/c/idea/106255>

## 🧱 Milestone Outputs
- Decision on CIP is documented via ADR in repository
- If CIP required or recommended, initial version is created
- Video showing indexer prototype solution on testnet or mainnet
- Initial version of OpenAPI definition for the services available at this time exists (1 to n, depending on services required at the time)
- Unit tests for core services available at this time have been written
- CI pipeline generates all required artifacts

## ✔ Acceptance criteria
- A decision has been made if writing a CIP is required, or if the documentation and existing standards can be sufficiently used and combined to achieve the desired outcomes.
- If a CIP is recommended/required, the initial proposal exists as a branch of a CIP repository fork (https://github.com/cardano-foundation/CIPs)
- Initial API definition for interacting with the service(s) defined and can be used with those services that have a working version
- Docker images and tests exist and are created/run as part of a CI pipeline

## 🧾 Evidence of milestone completion	
- Short video showcasing the on-chain indexing is available via private link on YouTube
- Unit test reports for services show a total 80%+ coverage
- Docker images are available in a public docker registry
- All other evidence will be provided either as content of the public repository or as links that are available from the public repository

## 🚀 Results
The following sections provide all documentation and context and detailed links for each output where applicable:

### Indexing Video
A YouTube video link that showcases the on-chain indexing solution, including a prototype site to interact with the verification service can be found below. Indexing for CIP-0066 verification can be done via Cardano DB sync (default) or Blockfrost (also shown in video):

<https://youtu.be/g2G-_nJnxMs>

The video shows the creation of an IAMX DID with Twitter social media verification, the minting of a new policy on the preview network and creation of the corresponding DID NFT token. The scripts to mint these tokens are provided here: [Example Scripts](https://gitlab.com/vibrantnet/ryp/-/tree/main/example/scripts). 
They can be run on any Linux server that has a bash shell and a working cardano-node and cardano-cli installed (8.1.2 or newer). The prototype site interacts with the preview network and is available at <https://ryp.vibrantnet.io>

### Unit Tests
Unit test report shows 93%+ coverage at the time of submission and can be publicly viewed at any time here: <https://sonarcloud.io/summary/overall?id=vibrantnet_ryp>

### Docker Images
The following docker images are built as part of the pipeline and publicly available:

- <https://gitlab.com/vibrantnet/ryp/container_registry/5912472> (Verification)
- <https://gitlab.com/vibrantnet/ryp/container_registry/5912544> (Publishing)
- <https://gitlab.com/vibrantnet/ryp/container_registry/5912538> (Subscription)

Together with the documentation provided in the repository, they can be used by anyone to use the current version of the core services. The services, their required environment variables and docker images are also referenced in the docker-compose.yml file that can be used to quickly spin up RYP: <https://gitlab.com/vibrantnet/ryp/-/blob/main/docker-compose.yml?ref_type=heads>

### CIP Decision
The decision on if a CIP should be written at this time was made after research and consulting with current CIP editor Adam Dean. The decision is that no CIP is recommended currently, and its reasoning is outlined here: [0004-cip-suggestion.md](../decisions/0004-cip-suggestion.md)

### OpenAPI Definitions
The OpenAPI definitions for all three core services have been designed in Stoplight.io and are available in the repository at https://gitlab.com/vibrantnet/ryp/-/tree/main/docs/api

They can be loaded into an API tool like Postman or Insomnia and be used to interact with the REST APIs of the services if deployed locally or remotely.

In the same folder, alongside the definitions, there is also an openapi-generator script that was used to bootstrap the core service APIs. It can be triggered for each service using the following commands:

```
npm run build-core-verification-api
npm run build-core-subscription-api
npm run build-core-publishing-api
```

### Other Build Artifacts
All artifacts required are built as part of the pipeline: [.gitlab-ci.yml](../../.gitlab-ci.yml)

This includes the docker images, code coverage reports and JAR files of the core services (even though they are not directly used, and only provided for the later build stage where the docker images are created).