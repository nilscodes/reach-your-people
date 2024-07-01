import { generateNonce, checkSignature, DataSignature, readTransaction, resolveSlotNo } from '@meshsdk/core';
import { redisClient } from './nonce-cache';
import { toBaseAddress, calculateTxHash } from '@meshsdk/core-csl';
import { Vkeywitness } from '@sidan-lab/sidan-csl-rs-nodejs';

const verificationNetwork = process.env.CARDANO_NETWORK ?? 'mainnet';

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

export function verifyTransaction(signedTxCbor: string): boolean {
  const txJson = readTransaction(signedTxCbor);
  const stakeDelegation = txJson.body.certs?.find((cert: any) => cert.StakeDelegation !== undefined);
  if (stakeDelegation) {
    const stakeKey = stakeDelegation.StakeDelegation?.stake_credential.Key;
    try {
      // Only continue if a staking delegation key is attached
      if (!stakeKey) {
        return false
      }
      // Only continue if the TX is not older than 300 slots (5 minutes on mainet)
      const transactionExpiresAtSlot = txJson.body.ttl;
      const currentSlot = +resolveSlotNo(verificationNetwork, new Date().getTime());
      if (currentSlot - 300 > transactionExpiresAtSlot) {
        return false;
      }
      const outputs = txJson.body.outputs.map((output: any) => output.address);
      const firstAddr = outputs[0];
      // Only continue if all outputs of the expired tx go to the same address
      if (!outputs.every((addr: string) => addr === firstAddr)) {
        return false;
      }
      const outputStakeAddressHash = toBaseAddress(firstAddr)?.stake_cred()?.to_keyhash()?.to_hex();
      // Confirm that stake address from transaction is the same as the one in the certificate
      if (outputStakeAddressHash !== stakeKey) {
        return false;
      }

      const hash = calculateTxHash(signedTxCbor);
      const hashBytes = Buffer.from(hash, 'hex');
      const signatories = txJson.witness_set.vkeys;
      return signatories.every((witness: any) => {
        const w = Vkeywitness.from_json(JSON.stringify(witness))
        return w.vkey().public_key().verify(hashBytes, w.signature())
      });
    } catch (e) {
      console.error(e);
    }
  }
  return false;
}