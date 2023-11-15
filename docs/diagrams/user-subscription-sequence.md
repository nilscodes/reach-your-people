# Subscription Sequence Diagram

## 💡 Purpose
This shows sequence of events that occurs when a new user wants to subscribe to a project named A, using notifications through a social media account at X.

### Assumptions
The user is completely new to the service and has no previous verified wallets and not yet authorized the RYP application to interact with the social media app.

## 🖼️ Diagram

### Wallet Verification and Social Media Authentication Sequence

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant S as Subscription Service
    participant V as Verification Service
    participant X as Social Media App
    U ->> +S: Subscribe to Project A
    S -->> -U: Please verify a wallet first
    U ->> +V: Verify this wallet
    V -->> +U: Please sign this data/transaction
    U ->> -V: Send signed data/transaction
    V ->> V: Confirm signature
    Note right of V: Signature verification<br>on backend
    V -->> -U: Wallet confirmed, please select your notification service
    activate U
    U ->> +V: Use Social Media App X
    deactivate U
    V -->> U: Please authorize your account with X
    activate U
    U ->> +X: Allow Verification Service to access X account
    deactivate U
    X -->> -V: User has confirmed access
    V -->> +S: Confirm X account
    deactivate V
    S ->> S: Store subscription for project A in database
    Note right of S: Subscription request<br>from step 1 is remembered
    S -->> -U: Subscription confirmed
```