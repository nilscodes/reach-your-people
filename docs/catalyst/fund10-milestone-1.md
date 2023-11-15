---
status: completed
date: 2023-11-15
---
# 1️⃣ Architecture Modeling and Research	

## 💡 Purpose
This document outlines the goals and outcomes of Milestone 1 of the Catalyst Fund 10 project "Reach your people - social media and messaging integrations for DAOs and NFT projects" (further referenced to as RYP).

The  contains the project ID, project and milestone links, the outputs, acceptance criteria and evidence provided for the Proof of Achievement.

## 🆔 Project ID
`1000156`

## 🔗 Important Links

- Catalyst: <https://projectcatalyst.io/funds/10/f10-daos-less3-cardano/reach-your-people-social-media-and-messaging-integrations-for-daos-and-nft-projects>
- Milestone Module: <https://milestones.projectcatalyst.io/projects/1000156/milestones/1>
- Ideascale: <https://cardano.ideascale.com/c/idea/106255>

## 🧱 Milestone Outputs
- Research of existing CIPs for use in this protocol to create the below outputs
- Architecture Diagram (x1)
- ADR on implementation language(s) (x1)
- ADR on first integration (x1)
- Sequence Diagram for first integration (x1)
- Public Issue Tracker
- Public Repository

## ✔ Acceptance criteria
- Architecture for service components, on-chain components, determine best candidates for on-chain and off-chain aspects are clearly understood from the architecture diagram
- Sequence diagram explains the flow of messages between publisher, blockchain, our service and the message recipient, for the initial integration implementation
- An ADR (Architectural Design Record) has been added to the repository outlining the programming language choice(s)
- An ADR has been added to the repository outlining the first integration that will be built for the protocol (i.e. which messaging app/social media integration will be used for primary development, and why)
- Links to the public repository and issue tracker are made available

## 🧾 Evidence of milestone completion	
- Link to public repository
- All other evidence will be provided as a dedicated document for this milestone, stored in the public repository. It will include the required links to other documents in the repository or external links, as well as descriptions of the outputs.

## 🚀 Results
The following sections provide all documentation and context and detailed links for each output where applicable:

### Research of existing CIPs for use in this protocol to create the below outputs
Various CIPs have been read and analyzed with regards to applicability for this project:

- [CIP-0008](https://cips.cardano.org/cips/cip8/)
- [CIP-0022](https://cips.cardano.org/cips/cip22/)
- [CIP-0025](https://cips.cardano.org/cips/cip25/)
- [CIP-0066](https://github.com/cardano-foundation/CIPs/pull/294)
- [CIP-0067](https://cips.cardano.org/cips/cip67)
- [CIP-0068](https://cips.cardano.org/cips/cip68)
- [CIP-0072](https://cips.cardano.org/cips/cip72/)
- [CIP-0089](https://github.com/cardano-foundation/CIPs/pull/466)
- [CIP-1694](https://cips.cardano.org/cips/cip1694/)

The findings are reflect in various choices for the initial architecture as well as authentication and verifications flows below. In addition, certain decisions could be made to exclude the need to spend effort on implementing some logic, like CIP-0089 not being relevant for RYP at this time.

We have also reached out to [IAMX](https://iamx.id) (co-authors of this CIP) with regards to CIP-0066 to further discuss how the solution can be best used for the initial version of RYP.

With CIP-1694 on the horizon, it was also worth investigating how easy it is to add dRep verification and dRep announcements to the tool.

### Architecture Diagram (x1)
The architecture diagram has been created. It uses mermaid, an embeddable definition language that can display diagrams within the context of markdown documentation. The diagram visuals are based on [Kubernetes Diagrams](https://kubernetes.io/docs/contribute/style/diagram-guide/), because Kubernetes is likely the deployment strategy for the services. It maps out the core services, integrations and external systems and explains the responsibilities of each individual service:

[Architecture Diagram](../diagrams/architecture.md)

### ADR on implementation language(s) (x1)
A decision was made on which programming languages to use for the core services, and why the respective languages were chosen:

[Any Decision Record: Implementation Languages](../decisions/0001-build-core-backend-services-in-kotlin-and-typescript.md)

### ADR on first integration (x1)
A Decision Record has been created and Discord was chosen as the first integration that will be developed (Milestone 3+4):

[Any Decision Record: First Integration](../decisions/0002-first-messaging-integration-discord.md)

### Sequence Diagram for first integration (x1)
After evaluating the architecture and reading through the CIPs, we created two sequence diagrams (using mermaid, as described above), showing two interaction flows:

1. Subscribing to a project and verifying your wallet and then social media account:
[Wallet Verification and Social Media Authorization](../diagrams/user-subscription-sequence.md)
2. A community manager from a project submitting an announcement, using CIP-0066 for verification: [Publishing for a Project](../diagrams/project-publishing-sequence.md)

Additional sequence diagrams to deal with questions around error handling or other scenarios will be added in the coming milestones as needed.

### Public Issue Tracker
We created a public Atlassian Jira instance and successfully applied for an Open-Source license for it. The public link is:

<https://vibrantnet.atlassian.net/>

In addition to creating the tracker, a roadmap including all milestones as epics and all outputs as tasks in these epics was created. It is only accessible for users who create a Jira account (it is not technically possible to make it publicly viewable. A link and screenshot are linked below)

- [RYP Roadmap (requires login to Atlassian)](https://vibrantnet.atlassian.net/jira/software/c/projects/RYP/boards/2/timeline)
- [RYP Roadmap Screenshot](./fund10-milestone-1-roadmap.png)

Additional stories and tasks for the actual implementation will be created throughout the coming milestones. Participation by the community is encouraged.

### Public Repository
A public GitLab repository has been created and this document is part of it.

Link: <https://gitlab.com/vibrantnet/ryp>