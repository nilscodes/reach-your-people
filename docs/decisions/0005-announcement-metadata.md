---
status: accepted
date: 2024-02-25
deciders: Nils Codes
---
# Activity Streams 2.0 will be used for Publishing Metadata

## Context and Problem Statement

To be able to publish announcements, projects and individuals will need to submit their information to the RYP service. While this can happen with a tailored UI, the announcement will need to be exchanged between services and potentially stored on-chain or in a decentralized storage solution. To make this decision, we want to investigate the different standards that could be used for the metadata exchange and storage.  

## Decision Drivers

* Publishing metadata is a flexible but standardized format
* Tooling and libraries are available for the chosen format or can be easily built
* If possible, the format should align with other standards used in the Cardano ecosystem
* Metadata should be able to be published on-chain or off-chain
* The format should be extensible to allow adjusting to the needs of the various integrations

## Considered Options

* Custom JSON format
* WebSub (formerly Pubsubhubbub)
* ActivityPub
* Activity Streams 2.0

## Decision Outcome

After completing the research for [Social Web Protocols](https://www.w3.org/TR/social-web-protocols) at the W3C and other sources, the decision was made to use the [Activity Streams 2.0](https://www.w3.org/TR/activitystreams-core/) (AS 2.0) format for the metadata exchange within RYP.

### Activity Streams 2.0

AS 2.0 is a social media-focused data model to define activities, based on JSON-LD. JSON-LD is also the standard of choice for various other CIPs in the Cardano Ecosystem. This includes [CIP-0066](https://github.com/cardano-foundation/CIPs/pull/294), which we use for Identity Verification, as well as the new governance metadata standard [CIP-0100](https://cips.cardano.org/cip/CIP-0100). It is a flexible format that can be used to represent a wide variety of activities, as described in the [AS 2.0 Vocabulary](https://www.w3.org/TR/activitystreams-vocabulary/). It has many features not needed for the initial version of RYP, but leveraging links and the Announcement object type will allow us to exchange and store announcements in a consistent way and allow for various on-chain and off-chain use cases. We will support direct submission of AS 2.0 announcements via API or a custom interface, with a potential option to mint them directly on chain at a later point.

An example announcement in AS 2.0 format for an individual could look like this:

```json
{
  "@context": "https://www.w3.org/ns/activitystreams",
  "type": "Announce",
  "actor": {
    "type": "Person",
    "name": "Nils Codes",
    "url": "https://twitter.com/nilscodes"
  },
  "object": {
    "type": "Note",
    "content": "I just published a new project on RYP. Check it out!"
  }
}
```

A DAO could announce a workshop as follows:

```json
{
  "@context": "https://www.w3.org/ns/activitystreams",
  "type": "Announce",
  "actor": {
    "type": "Organization",
    "name": "Cardano DAO",
    "url": "https://cardano.org"
  },
  "object": {
    "type": "Event",
    "name": "Cardano DAO Workshop",
    "content": "Join us for a workshop on the future of Cardano",
    "url": "https://cardano.org/workshop",
    "startTime": "2024-02-25T14:00:00Z",
    "endTime": "2024-02-25T16:00:00Z"
  }
}
```

The format is flexible and lets projects, companies, DAOs and individual users create different types of announcements containing additional metadata. Is also extensible to allow for customized fields that might be required to allow different integrations to work - like longer or shorter announcements based on the platform (think Email vs. text messages), and various formats that may or may not be able to include multimedia content or markdown.

AS 2.0 is widely adopted through [ActivityPub](#activitypub) and the social networks that implement ActivityPub, like [Mastodon](https://joinmastodon.org/), [Meta Threads](https://www.theverge.com/2023/12/15/24003435/adam-mosseri-threads-fediverse-plans), and the services that are part of [Fediverse](https://fediverse.party/), a network of open-source social media platforms. This means that the format is well understood and has a large number of users and developers actively working on it.

Other options investigated as part of this decision were:

### WebSub (formerly Pubsubhubbub)
WebSub was deemed not suitable because subscribers need to provide a GET endpoint, and as such would have to host their own services. The protocol could be used internally between the different core services of RYP, but this would create overhead that is not necessary. We can also envision using WebSub as a connector and a dedicated integration (alongside Discord, Email etc.).

### ActivityPub
Activity Pub is a decentralized social networking protocol that is based on Activity Streams 2.0. It has lots of capabilities but is mostly API driven and only partially usable for this project, since we do not aim to build a new social media protocol, but instead connect blockchain to existing protocols. It would be possible to use some of the ActivityPub logic for the internal communication between services and to create a reusable standard for new connectors to be reached by the core services.

### Custom Format
A custom format was considered but quickly abandoned because of the required effort to create and maintain it, while it would mostly just replicate the capabilities of AS 2.0. It would also not be as widely understood and used as AS 2.0. The benefits of being able to tailor the format to the needs of Cardano and RYP were not seen as significant enough to outweigh the downsides.