---
status: accepted
date: 2023-11-13
deciders: Nils Codes
---
# Build Core Backend Services in Kotlin and TypeScript

## Context and Problem Statement

A decision has to be made which language(s) "Reach Your People" should be written in. For this project, a language must be chosen that is not foreign to the Cardano ecosystem, and well-known enough by the initial repository maintainers. It must be possible to make progress at the pace required to implement the Catalyst proposal on time. Dependencies with other tools need to be considered, including the availability of Web 2.0 applications that this project aims to integrate with. Another important aspect is the ability to leverage existing libraries for the Cardano blockchain.

## Considered Options

* Java
* Kotlin
* TypeScript
* JavaScript
* PHP
* Rust
* Python
* Haskell

## Decision Outcome

Chosen options
- **Kotlin**
- **TypeScript**

There are various JVM-based libraries on Cardano (i.e. Java and Kotlin compatible, example: [Cardano Client Lib](https://github.com/bloxbean/cardano-client-lib)). There is also significant experience by larger projects building Cardano integrations in Kotlin (example: [NEWM](https://github.com/projectNEWM)). The core maintainers of "Reach Your People" have built multiple applications in Kotlin that interact with Cardano (all part of the [Vibrant](https://github.com/nilscodes/hazelnet) repository) and as such will be able to quickly bootstrap the services needed for this project. Due to the established relationships to developers from other Kotlin/Java-based Cardano projects, it will also be possible to get help from external resources if needed.

For services where Kotlin is not the best choice, due to limited libraries with transaction-related processing capabilities (like [Lucid](https://lucid.spacebudz.io/)), services may be built in TypeScript. The current core maintainers of "Reach your People" are knowledgeable in it and there is also a large body of libraries available for Cardano and other chains that would be interoperable.

Both languages are easily deployed via containers and can leverage the well-established and solid open-source frameworks for backend service development (like Spring Boot and Express).

PHP, Python and Rust were other options considered, but due to the low availability of libraries for PHP (there is no category for it in the [Cardano Developer Portal](https://developers.cardano.org/tools)) and the lack of expertise of the core maintainer in Python and Rust, they were not further considered. Java was ousted due to higher boilerplate code requirements and lesser functional programming expressiveness, while in JavaScript as a dynamically typed language, it is too easy to write erroneous code.

Haskell is not a language that makes it easy to build web applications, and despite the obvious overlap with Cardano (many Cardano core projects are written in Haskell), finding competent Haskell developers is a challenging task.

This decision does not automatically prescribe which languages can be used for any of the messaging/social media integrations or for any frontends that allow connecting to the core services.

### Consequences

Positive

* The application prototype will be more quickly bootstrapped and containerized due to pre-existing knowledge and codebase
* Any JVM-based libraries can be integrated into the codebase, even if not written in Kotlin (Java)
* Any npm packages can be integrated into the codebase, even if not written in TypeScript (JavaScript)
* Any libraries written as part of the application can be used in other JVM-based (or npm-based) applications
* Both are statically typed languages, making it easier for less experienced developers to write safe code
* Even if not containerized, both languages are OS-independent
* Kotlin has notably less boilerplate code than Java
* Both languages support aspects of functional programming

Negative
* Bringing on board other maintainers may be harder due to the smaller footprint in the software development landscape with regards to Kotlin
* Both languages are slightly harder to learn than their more established counterparts (this assessment is based on ten years of experience onboarding software developers at a Technology company that uses all of these languages)
* Performance maxmiums may not be as high as for example with Rust
