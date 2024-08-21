---
status: accepted
date: 2024-08-20
deciders: Nils Codes
---
# The individual integrations are responsible for customizing the messages they send

## Context and Problem Statement

The RYP project will have multiple integrations with various services, such as Discord, Telegram, and Twitter. Each of these services has different requirements for the messages that are sent to them. For example, Discord allows for rich embeds, while Push API does not. The messages sent to these services should be customized to fit the requirements of the service they are sent to. This means that localization and combining the metadata in a way that is useful for the service is necessary.

## Decision Drivers

* Internationalization of messages should be easy
* Localized messages should be consistent across integrations as much as possible
* Formatting messages needs to include metadata that may be different by announcement type
* The messages sent by the integrations should be customized to fit the requirements of the service they are sent to.

## Considered Options

* Translation and building messages in core-publishing
* Translation and building messages in the integrations

## Decision Outcome

Chosen option: Translation and building messages in the integrations.

We will have the integrations be responsible for customizing the messages they send. This will allow for more flexibility in the messages that are sent to each service. The `core-publishing` service will provide the integrations with the necessary metadata, and the integrations will be responsible for building the messages based on this metadata.

This keeps the `core-publishing` service simple and focused on its main task of publishing announcements and admonishing metadata. It allows the services to entirely own the messages they send. However, this also means that additional work is required for each integration even if it can simply use the basic data and does not need to do major processing.

To mitigate this issue, a shared library was created in [/integrations/shared](../../integrations/shared) that allows easy access to standardized message building. It will also be responsible for the consistency of translations and hold all default localization files. This will allow integrations to be built with the minimum amount of work, while still keeping all the flexibility needed to customize the messages to the specific requirements of the service if required.

## Consequences

The overhead of maintaining the shared library is considered acceptable, as it will be used by all integrations and will be maintained by the core team. If integrations in other languages than JavaScript are required, additional libraries may have to be created.

The payload sent to the queues will be larger, potentially leading to slowdowns and higher resource costs. In the future this could be optimized by providing announcement metadata via a redis cache that each integration can access, instead of sending the shared data along with each message.