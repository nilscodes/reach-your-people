---
status: accepted
date: 2024-01-13
deciders: Nils Codes
---
# No CIP will be written at this time

## Context and Problem Statement

One of the goals of the research phase is to determine if there is value in creating a Cardano Improvement Proposal (CIP) for any of the on-chain elements of the RYP project. Both the verification and publishing core services, and to a lesser likelihood, the subscription core service, could potentially use on-chain data for some of their capabilities. To make this decision, we want to investigate the different standards that could be used with a new CIP as well as relevant existing CIPs.

## Decision Drivers

* New CIP does not overlap significantly with existing CIP
* New CIP provides information about on-chain entities or behaviors required to provide RYP core functionality

## Steps to ensure informed decision

1. Research existing CIPs outlined in [Milestone 1 Document](../catalyst/fund10-milestone-1.md)
2. Research verification and publishing standards from the W3C and IETF
3. Meet with a CIP editor to discuss research and finalize decision

## Decision Outcome

After the final meeting with CIP editor Adam Dean, the decision to not propose a new CIP at this time was made. There are several existing and emerging standards in place for verification:

- [CIP-0022](https://cips.cardano.org/cips/cip22/) for stake pool operators
- [CIP-0066](https://github.com/cardano-foundation/CIPs/pull/294) and [CIP-0088](https://cips.cardano.org/cips/cip88/) for verification of token-based projects
- [CIP-1694](https://cips.cardano.org/cip/CIP-1694) and a yet to be determined CIP for dRep verification

Since there are enough standards in place for verification, the remaining considerations focus on the publishing flow. For the publishing metadata and exchange format, a subset of the [Activity Streams 2.0 Announce activity type](https://www.w3.org/TR/activitystreams-vocabulary/#dfn-announce) has been chosen. Detailed information and sample usage can be found in [MADR-0005](./0005-announcement-metadata.md). The remaining researched involved to what extent it would be useful to publish this metadata either as part of the API-based publishing provided by the core services. Another potential use case was allowing project owners to publish the announcement in metadata directly or via anchors (like in [CIP-0100](https://cips.cardano.org/cip/CIP-0100)) on chain. An indexing solution could then react to published announcements without any API calls.

However, any data put on chain may be considered bloat if not used widely. Data for a potentially not yet accepted standard (which usually comes with a metadata label) could pollute blocks and unnecessarily increase chain size. In addition, it would unnecessarily complicate the verification flow during publishing, in particular for publishers that do not have native signatures (like SPOs, dReps, and individual wallets). NFT/FT projects may have time-locked policy scripts that become inaccessible, and they may not want to have project announcements on their main policy IDs. In addition, publishing by minting tokens or submitting transactions with metadata would require ADA to publish.

All in all, no compelling reasons to create a publishing standard were found at this time. As the project evolves and users leverage the application and DID solutions emerge further, we will revisit the on-chain publishing approach and a potential CIP.

## Contributors

- Adam Dean <https://twitter.com/adamKDean>

