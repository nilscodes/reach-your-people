# Publishing Sequence Diagrams

## Publishing for a Project

### 💡 Purpose
This diagram showcases the basic flow of events when a person that manages a project wants to publish an announcement. In this example, the users authorization is verified by means of connecting a social media account for the platform X (formerly Twitter), which is confirmed to be the official account for the project via [CIP-0066](https://github.com/cardano-foundation/CIPs/pull/294) on-chain.

This diagram focuses on the successful flow and does not model any error conditions.

### ⚠️ Assumptions
The person is completely new to the service and has not previously verified that they have permissions to publish on behalf of the project.

### 🖼️ Diagram

```mermaid
sequenceDiagram
    autonumber
    actor P as Person from Project A
    participant PUB as Publishing Service
    participant V as Verification Service
    participant X as Social Media App
    participant C as Cardano Ledger
    participant Q as Queue
    P ->> +PUB: Publish Announcement for Project A
    PUB -->> -P: Please verify that you are<br>authorized to publish for Project A
    activate P
    P ->> +V: Use Social Media App X
    deactivate P
    V -->> P: Please authorize your account with X
    activate P
    P ->> +X: Allow "Reach Your People" to access X account
    deactivate P
    X -->> -V: User has approved access
    V ->> +C: Verify X account is<br>authorized to publish (via CIP-0066)
    C -->> -V: CIP-0066 identity confirmed
    V ->> +PUB: Confirm this X account<br>is allowed to publish
    deactivate V
    PUB -) Q: Publish announcement
    PUB -->> -P: Announcement published
```
