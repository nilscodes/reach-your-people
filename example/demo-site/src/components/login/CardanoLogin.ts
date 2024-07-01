import { RypSiteApi } from "@/lib/api";
import { BrowserWallet } from "@meshsdk/core";
import { signIn } from "next-auth/react";

export default async function cardanoWalletLogin(selectedWallet: string, api: RypSiteApi) {
  const activeWallet = await BrowserWallet.enable(selectedWallet);
  const rewardAddresses = await activeWallet.getRewardAddresses();
  const stakeAddress = rewardAddresses[0];
  const addresses = await activeWallet.getUsedAddresses();
  const nonceResponse = await api.createNonce(addresses[0], stakeAddress);
  const signature = await activeWallet.signData(stakeAddress, nonceResponse.nonce);
  signIn("cardano", {
    stakeAddress,
    signature: JSON.stringify(signature),
    callbackUrl: '/account',
  });
}
