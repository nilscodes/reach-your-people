import { generateNonce, checkSignature, DataSignature } from '@meshsdk/core';
import { nonceCache } from './nonce-cache';

type NonceCacheEntry = {
  nonce: string;
  userAddress: string;
  stakeAddress: string;
};

export function createNonce(userAddress: string, stakeAddress: string): string {
  const nonce = generateNonce('Verifying your wallet for RYP: ');
  nonceCache.set(stakeAddress, {
    nonce,
    userAddress,
    stakeAddress,
  });
  return nonce;
}

export function verifySignature(signature: DataSignature, stakeAddress: string): boolean {
  const nonceCacheEntry = nonceCache.get(stakeAddress) as NonceCacheEntry | undefined;
  if (nonceCacheEntry) {
    return checkSignature(nonceCacheEntry.nonce, nonceCacheEntry.stakeAddress, signature);
  }
  return false;
}
