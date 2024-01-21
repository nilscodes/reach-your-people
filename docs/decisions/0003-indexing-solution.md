---
status: accepted
date: 2024-01-07
deciders: Nils Codes
---
# Cardano DB sync will be the primary indexer for the RYP prototype

## Context and Problem Statement

Part of the requirements for allowing valid announcements to be made is that [CIP-0066](https://github.com/cardano-foundation/CIPs/pull/294) compliant DIDs will have to be verified as part of the publishing process. Since these are often referenced through a collection token (which then points to the IPFS-stored DID), we will need to be able to access historic transaction metadata for collection tokens so we can do the DID lookup and subsequent verification of social media accounts. In addition, we want to be able to support an on-chain publishing mechanism where the verification is built-in by means of signature validation or policy validation. We would like to find one or more indexing solutions that can cover these use cases.

## Decision Drivers

* Can index transaction metadata by label for CIP-0066 token discovery
* Quick during lookups (sub 100ms response times)
* Is decentralized (i.e. can be deployed by anyone)
* Does not require building additional persistence code or indexing logic (i.e. needing a database is fine, but having to write additional code to store it should be not required)
* Can be used with SanchoNet (the governance testnet of Cardano)

## Considered Options

* [Oura](https://txpipe.github.io/oura/)
* [Ogmios](https://ogmios.dev/)
* [Kupo](https://cardanosolutions.github.io/kupo/)
* [Cardano DB sync](https://github.com/IntersectMBO/cardano-db-sync)
* [Carp](https://dcspark.github.io/carp/docs/intro/)
* [Blockfrost](https://blockfrost.io/)
* [Koios](https://www.koios.rest/)
* [Scrolls](https://github.com/txpipe/scrolls)

## Decision Outcome

The initial implementation of RYP will use Cardano DB sync for looking up on-chain verification metadata. Based on a comprehensive comparison of the various available indexing solutions and consideration of the various decision drivers mentioned above, only DB sync fulfilled the crucial criteria to require no additional persistence logic to be implemented, and to support the governance testnet. While there is a noticeable downside in that a DB sync instance of mainnet is very resource-intensive, this is mitigated by the fact that I have two such instances already in use for another project. In addition, on-demand services like [Demeter.run](https://demeter.run) allow projects to use DB sync without deploying their own instance. A dedicated version for SanchoNet is available, as well as an easy way to deploy DB sync for both Preview and PreProd networks.

A short summary of the other indexing candidates and their advantages and disadvantages as it relates to this project:

### Oura
Oura is a great indexer for responding to on-chain events and for very targeted ingestion of information, with its customizable filters and sinks. However, the need to implement your own persistence, rollback and destructuring logic required for long-term stored content makes it too much of a hurdle for an "easy-to-start" solution. Oura is a great follow-up indexer that could be used by RYP in the future, if time can be made to write custom filtering and persistence logic. If the ability to respond to on-chain publishing events is added in the future, this is also a great opportunity to leverage Oura. Because it receives data directly from cardano nodes, it would be able to generally collect data on SanchoNet.

### Carp
Carp is a SQL-based alternative to Cardano DB sync, but due to its more complex setup harder to get initially deployed. The filtering system allows to reduce the footprint, bringing down the hard drive requirements for mainnet to 30 GB, but reducing it to the exact metadata labels needed for RYP would require additional work in Rust (most of the 30 GB is NFT metadata, which is not needed for this project). No SanchoNet support exists.

### Blockfrost and Koios
These API-based solutions require regular queries and do not guarantee quick responses at all times, as experience with other tools (WalletBud) has shown. They also can come at an unpredictable cost, as there is no knowledge of the required call counts required to properly run the application. No SanchoNet support exists.

### Scrolls
Scrolls is designed to index a very specific set of metadata and could be used to index all metadata with the CIP-0066 label for example. However the builtin reducers do not have this capability, so implementation work in Rust would be required, which disqualifies it as an indexer for our project.

### Ogmios
Ogmios has many of the useful capabilities of Oura and would likely be a good solution to leverage if there would be time to implement a custom filtering and persistence algorithm. Because it receives data directly from cardano nodes, it would be able to generally collect data on SanchoNet.
Ogmios (no historic lookups without our own persistence and filtering logic, might be useful for listening for publishing transactions but not DID lookup)

### Kupo
Kupo has a great filtering system that would allow us to specific limit the indexed data to the CIP-0066 relevant metadata labels. However, it is not suitable because it uses a built-in database system that can by default only be queried through the web API provided by a second Kupo service. This service unfortunately does only allow filtering the indexed results in a very limited capacity, making it unusable for the type of indexing we need to do.
