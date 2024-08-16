
export function getWalletDisplayName(stakeAddress: string): string {
  return stakeAddress.substring(0, 11) + '…' + stakeAddress.substring(stakeAddress.length - 3);
}

export function getPolicyIdDisplayName(policyId: string): string {
  return policyId.substring(0, 11) + '…' + policyId.substring(policyId.length - 5);
}

export function getStakepoolHashDisplayName(poolHash: string): string {
  return poolHash.substring(0, 11) + '…' + poolHash.substring(poolHash.length - 5);
}

export function getDRepIdDisplayName(drepId: string): string {
  return drepId.substring(0, 11) + '…' + drepId.substring(drepId.length - 5);
}
