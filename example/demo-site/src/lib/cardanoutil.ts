
export function getWalletDisplayName(stakeAddress: string): string {
  return stakeAddress.substring(0, 11) + '…' + stakeAddress.substring(stakeAddress.length - 3);
}

export function getPolicyIdDisplayName(policyId: string): string {
  return policyId.substring(0, 11) + '…' + policyId.substring(policyId.length - 5);
}
