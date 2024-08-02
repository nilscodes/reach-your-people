import { Account } from "./ryp-subscription-api";

export function isPremiumAccount(account: Account): boolean {
  return account.premiumUntil !== undefined && new Date(account.premiumUntil) > new Date();
}