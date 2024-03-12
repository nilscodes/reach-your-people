import { generateNonce, checkSignature, DataSignature } from '@meshsdk/core';
import { redisClient } from './nonce-cache';

type NonceCacheEntry = {
  nonce: string;
  userAddress: string;
  stakeAddress: string;
};

export async function createNonce(userAddress: string, stakeAddress: string): Promise<string> {
  const nonce = generateNonce('Verifying your wallet for RYP: ');
  const nonceEntry: NonceCacheEntry = { nonce, userAddress, stakeAddress };
  await redisClient.set(`nonceCache:${stakeAddress}`, JSON.stringify(nonceEntry), {
    EX: 600, // Set the expiry time (in seconds)
  });
  return nonce;
}

export async function verifySignature(signature: DataSignature, stakeAddress: string): Promise<boolean> {
  const nonceEntryString = await redisClient.get(`nonceCache:${stakeAddress}`);
  if (nonceEntryString) {
    const nonceCacheEntry: NonceCacheEntry = JSON.parse(nonceEntryString);
    return checkSignature(nonceCacheEntry.nonce, nonceCacheEntry.stakeAddress, signature);
  }
  return false;
}
