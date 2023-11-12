---
status: accepted
date: 2023-11-11
deciders: Nils Codes
---
# Build Core Backend Services in Kotlin and TypeScript

## Context and Problem Statement

In any software project, to start implementing services, a decision has to be made in which language the implementation should be. For this project, a language must be chosen that is not foreign to the Cardano ecosystem, but well-known enough by the initial maintainers, that progress can be made at the pace required to implement the Catalyst proposal on time. Dependencies with other tools need to be considered, including libraries for both the Web 2.0 integrations that we will connect with, as well as the ability to leverage existing libraries for Cardano.

## Considered Options

* Java
* Kotlin
* TypeScript
* JavaScript
* PHP
* Rust

## Decision Outcome

Chosen options: Kotlin and TypeScript

There is a an existing ecosystem for JVM-based libraries on Cardano (i.e. Java and Kotlin compatible, example: [Cardano Client Lib](https://github.com/bloxbean/cardano-client-lib)), as well as significant experience by larger projects with building Cardano integrations in Kotlin (example: [NEWM](https://github.com/projectNEWM)). The core maintainers have built multiple applications in Kotlin that interact with Cardano ([Vibrant](https://github.com/nilscodes/hazelnet)) and as such will be able to quickly bootstrap the services needed for this project, as well as get help from external resources if needed.

For those services where Kotlin is not the best choice, due to limited libraries with transaction-related processing capabilities (like [Lucid](https://lucid.spacebudz.io/)), services may be built in TypeScript. It is possible that there is no need to create any services in TypeScript, but the current core maintainers are knowledgeable in it and there is also a large body of libraries available for Cardano and other chains that would be interoperable.

Both languages are easily deployed via containers and can leverage many very established and solid open-source frameworks for backend service development (like Spring Boot and Express).

PHP and Rust were other options considered, but due to the low availability of libraries for PHP (there is no category for it in the [Cardano Developer Portal](https://developers.cardano.org/tools)) and the lack of expertise of the core maintainer in Rust, they were not further considered. Java was ousted due to higher boilerplate code requirements and lesser functional programming expressiveness, while in JavaScript as a dynamically typed language, it is too easy to write erroneous code.

This decision does not automatically prescribe which languages can be used for any of the messaging/social media integrations or for any frontends that allow connecting to the core services.

### Consequences

Positive

* The application prototype will be more quickly bootstrapped and containerized due to pre-existing knowledge and codebase
* Any JVM-based libraries can be integrated into the codebase, even if not written in Kotlin (Java)
* Any npm packages can be integrated into the codebase, even if not written in TypeScript (JavaScript)
* Any libraries written as part of the application can be used in other JVM-based (or npm-based) applications
* Both are statically typed languages, making it easier for less experienced developers to write safe code
* Even if not containerized, both languages are OS-independent
* Kotlin has notable less boilerplate code than Java

Negative
* Bringing on board other maintainers may be harder due to the smaller footprint in the software development landscape with regards to Kotlin
* Both languages are slightly harder to learn than their more established counterparts (this assessment is based on ten years experience of onboarding software developers into a Technology company that uses all of these languages)
* Performance maxmiums may not be as high as for example with Rust
