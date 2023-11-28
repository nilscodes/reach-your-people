# Subscription Sequence Diagrams

## Wallet Verification and Social Media/Messaging Authorization

### 💡 Purpose
This shows the sequence of events that occurs when a new user wants to subscribe to a project named A, using notifications through a messaging account on Discord. This diagram focuses on the successful flow and does not model any error conditions.

### ⚠️ Assumptions
The user is completely new to the service and has no previous verified wallets and not yet authorized the RYP (Reach Your People) application to interact with the messaging app.

### 🖼️ Diagram

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant S as Subscription Service
    participant V as Verification Service
    participant X as Messaging App
    U ->> +S: Subscribe to Project A
    S -->> -U: Please verify a wallet first
    U ->> +V: Verify this wallet
    V -->> +U: Please sign this data/transaction
    U ->> -V: Send signed data/transaction
    V ->> V: <br>Confirm signature
    Note right of V: Signature verification<br>on backend
    V -->> -U: Wallet confirmed, please select your notification service
    activate U
    U ->> +V: Use Messaging App Discord
    deactivate U
    V -->> U: Please authenticate with Discord
    activate U
    U ->> +X: Allow "Reach Your People" to access Discord account
    deactivate U
    X ->> -V: User has approved access
    V ->> +S: Confirm Discord account
    deactivate V
    S ->> S: <br>Store subscription for<br>project A in database
    Note right of S: Subscription request<br>from step 1 is remembered
    S -->> -U: Subscription confirmed
```
### 🔗 SVG Link
[/docs/diagrams/user-subscription-sequence-01.svg](./user-subscription-sequence-01.svg)