---
status: accepted
date: 2024-03-25
deciders: Nils Codes
---
# The second messaging integration will be text messages via Twilio

## Context and Problem Statement

To reach a wide audience, beyond just users that are familiar with Web-3 technology, we need to be able to integrate traditional notification systems. These systems should be safe, readily available to everyday users, without costing too much effort to integrate into RYP ("Reach Your People"). The messaging solution should be able to reach a wide audience across many countries, to support the goals of the project

## Decision Drivers

* Wide availability
* Accessible for non-technical users
* Good verification practices exist

## Considered Options

* Twitter
* E-mail (via a service like [Mailgun](https://mailgun.com) or [Sendgrid](https://sendgrid.com))
* Text Messages (via a service like [Twilio](https://twilio.com))
* Telegram
* Slack
* MS Teams

## Decision Outcome

Text message integrations via Twilio were chosen as the prototype for the second integration. SMS/Text Messages are undoubtedly one of the most readily available messaging solutions world wide. Through providers like Twilio or Bird, sending text messages is as easy as implementing a service that interacts with their APIs. It is possible to reach almost everyone with a working mobile phone via text message.

While not free or easy (see below), we feel it is one of the most impactful integrations to build, to showcase the potential of our solution.

### Consequences

Important considerations when choosing text messages as a solution:

### Message size

Announcements for text messages, if they are designed to be fit into normal text messages, need to be either be broken into segments or reduced in size. Alternatively, they need to be links to the actual announcements.

### Message styling

If limiting the integration to regular text messages, formatting and images cannot be included in announcements. Using MMS could alleviate that, but would increase both cost and limit end users that can be reached.

### Regulatory concerns

Interacting with mobile phone carriers requires strict adherence to local regulations of the countries and states that messaging integrations are used in. This means separate signups, costs and limitations are in place for each country that is supported, as well as region-specific laws that need to be followed (like GDPR in Europe).

### Cost

Since text messages are not generally free to send (and often to receive), a cost factors comes into play. This means the costs for these messages needs to be considered when offering SMS as a notification solution for RYP. To allow projects and consumers to leverage text messages, a pricing model may need to be integrated into RYP, when deployed for a global user base.
