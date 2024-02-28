import axios from "axios";

export type NonceResponse = {
  nonce: string;
}

export class RypSiteApi {
  constructor(private readonly baseUrl: string) {
    axios.defaults.withCredentials = true;
    this.baseUrl = baseUrl;
  }

  async createNonce(userAddress: string, stakeAddress: string): Promise<NonceResponse> {
    return (await axios.post(`${this.baseUrl}/wallets/nonce`, { userAddress, stakeAddress })).data;
  }

  async verifySignature(signature: string, stakeAddress: string): Promise<boolean> {
    return (await axios.post(`${this.baseUrl}/wallets/verify`, { signature, stakeAddress })).data;
  }

  async getBestHandle(stakeAddress: string): Promise<string> {
    return (await axios.get(`${this.baseUrl}/wallets/besthandle/${stakeAddress}`)).data;
  }

  async unlinkExternalAccount(accountId: number, externalAccountId: number): Promise<void> {
    axios.delete(`${this.baseUrl}/accounts/${accountId}/externalaccounts/${externalAccountId}`);
  }

}