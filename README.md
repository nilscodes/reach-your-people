# Reach your People (RYP)

## Name
The working title of this project is "Reach Your People". We will refer to it as RYP or "Reach Your People" in the documentation.

## Description
RYP is a web-based service that connects NFT projects, Decentralized Autonomous Organizations (DAOs), Stake pool operators, governance staff (dReps) and other decentralized organizations with their members on the public blockchain Cardano. It allows organizations to send notifications to their members, regardless of the communication channels they use. The service is designed to privacy-preserving for users that want to be in the know without exposing their wallet or social media information directly with projects. It leverages several Cardano standards ([CIPs](https://cips.cardano.org/)) to connect with wallets and help verify the various parties involved.

In the future, the goal is to integrate seamlessly with multiple Blockchains and allow public and private DIDs to be used as a source of identity for the members and projects.

## Use Case
*Imagine Alice and Bob are holders of a governance token for the $NICE DAO. Jeff is a community lead in the DAO. Alice and Bob are busy people, and Alice only has access to email on the go most of the week, while Bob has Discord, but never checks his notifications, since there are so many. He is fairly active on Twitter, though. Jeff is publishing a summary for the newest vote on how to use the treasury in 2024. He presses send, and an email pops up in Alice's inbox. A notification about a Twitter DM with the same content and a link to the proposal appears on Bob's phone, seconds later. The twist: Jeff does not know Alice or Bob. Nor does he know their wallet addresses, email address or Twitter handle. He does not need to, since his DAO uses "Reach Your People".*

## Installation
TBD (Milestone 6)

## Usage
All docker images are publicly available in the associated GitLab registry at <https://gitlab.com/vibrantnet/ryp/container_registry>. The images are tagged with the following schema: `registry.gitlab.com/vibrantnet/<service>:<version>`. The services can be deployed using the `docker-compose.yml` file in the root of the repository. The `docker-compose.yml` file is designed to be used with the `docker-compose` command-line tool. To run RYP locally, edit the `docker-compose.prod.yml` file to include the correct environment variables and run `docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d` to start the services.

## Support
Technical issues can be written up at <https://vibrantnet.atlassian.net> or submitted informally in the Vibrant Discord at <https://discord.gg/nzka3K2WUS>.

## Roadmap
The roadmap is documented in our public Jira instance at <https://vibrantnet.atlassian.net>. You will have to log in to see the roadmap. A screenshot of the Milestone 1 version of the roadmap is available here: [/docs/catalyst/fund10-milestone-1-roadmap.png](./docs/catalyst/fund10-milestone-1-roadmap.png)

## Contributing
Contributions will open once the Catalyst project concludes.

## Authors and acknowledgment
TBD

## License
This project uses the [Apache License 2.0](./LICENSE)

## Project status
Active