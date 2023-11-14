# Subscription Sequence Diagram

## 💡 Purpose
These two diagrams lay out the sequence of events for what happens when a new user would like to subscribe to a project named "A", using a social media account at "X". To reduce the complexity of the sequence diagram, the process has been split into two parts:

1. Verification of the wallet
2. Verification of X account

### Assumptions
The user is completely new to the service and has no previous verified wallets.

## 🖼️ Diagrams

### Wallet Verification Sequence

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant S as Subscription Service
    participant V as Verification Service
    U ->> +S: Subscribe to Project A
    S -->> -U: Please verify a wallet first
    U ->> +V: Verify this wallet
    V -->> +U: Please sign this data/transaction
    U ->> -V: Send signed data/transaction
    V ->> V: Confirm signature
    Note right of V: Signature verification<br>on backend
    V -->> -U: Wallet confirmed
```

### Social Media Authentication Sequence

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant S as Subscription Service
    participant V as Verification Service
    participant X as Social Media App
    U ->> +S: Subscribe to project A
    S -->> -U: Please select your notification service
    activate U
    U ->> +V: Use Social Media App "X"
    deactivate U
    V -->> U: Please authorize your account with "X"
    activate U
    U ->> +X: Allow access for "Verification Service"
    deactivate U
    X -->> -V: User has confirmed access
    V ->> +S: Confirm "X" account
    deactivate V
    S ->> S: Store subscription for project A in database
    Note right of S: Subscription request<br>from (1) is remembered
    S -->> -U: Subscription confirmed
```