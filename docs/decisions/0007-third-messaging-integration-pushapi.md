---
status: accepted
date: 2024-05-10
deciders: Nils Codes
---
# The third messaging integration will be anonymous notifications via the Push API and Browser Notifications

## Context and Problem Statement

So far, all integrations built for RYP required the end user to reveal information about their social media accounts or other personal data, like phone numbers. To showcase that (semi-)anonymous notification systems are also possible, we need build an integration that does reveal minimal information about the end user. It needs to be widely used by internet users, without notable limitations in geographical availability or accessibility.

## Decision Drivers

* Wide availability
* Accessible for non-technical users
* Does not require personal info to be provided

## Considered Options

* E-mail
* Push API via Browser Notification API
* Push API via custom mobile application
* WebSockets via custom mobile application

## Decision Outcome

We choose to build a browser-based Push API / Notification API solution, which combines the widely used Push API (for example used for mobile phone banners and notifications) and the web browser-based Notifications API. It requires no personal information to be exposed. The subscription data that is used to send notifications does not include anything identifying the user to the sender, except their Push API endpoint, which potentially reveals information about their browser. It is widely used, as Internet Standards exist for both parts of this integration:

- <https://w3c.github.io/push-api/>
- <https://developer.mozilla.org/en-US/docs/Web/API/Push_API>
- <https://notifications.spec.whatwg.org/>
- <https://developer.mozilla.org/en-US/docs/Web/API/Notifications_API>

It is secure as only the holders of the private so-called VAPID keys (which are used to register a push subscription) are able to send messages to the respective subscriptions.

E-mail was excluded due to the associated potential costs as well as going under the assumption that the majority of end users do not have disposable email addresses available to them. If they do, it likely would not qualify for our "Accessible for non-technical users" criteria.

WebSockets and Push API via custom App were rejected due to the amount of work required, in particular building a mobile app for either iOS or Android, and all the work involved in delivering it to the end users.

### Consequences

Important considerations when choosing browser notifications as a solution:

### Message size

Announcements for browser notifications need to be either be broken into segments or reduced in size. Alternatively, they need to be links to the actual announcements.

### Message styling

If limiting the integration to regular browser notifications, formatting and images cannot easily be included in announcements, while retaining support across browsers and operating systems.

### Revoking permissions

Users can revoke their Push API permissions without letting the subscription service know, so the service needs to handle expired subscriptions accordingly.