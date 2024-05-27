import axios from "axios";
import { Project, ProjectCreationRequest } from "./types/Project";
import { Subscription } from "./types/Subscription";
import { SubscriptionStatus } from "./types/SubscriptionStatus";
import { AnnouncementFormData } from "@/components/projects/PublishAnnouncement";
import { GetLinkedExternalAccounts200ResponseInner } from "./ryp-subscription-api";

// function getRandomDelay(): Promise<void> {
//   const delay = Math.random() * (500 - 30) + 30;
//   return new Promise((resolve) => { setTimeout(resolve, delay); });
// }

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

  async unlinkExternalAccount(externalAccountId: number): Promise<void> {
    axios.delete(`${this.baseUrl}/account/externalaccounts/${externalAccountId}`);
  }

  async getProjectsForAccount(): Promise<Project[]> {
    return (await axios.get(`${this.baseUrl}/account/projects`)).data;
  }

  async addNewProject(project: ProjectCreationRequest): Promise<Project> {
    return (await axios.post(`${this.baseUrl}/projects`, project)).data;
  }

  async publishAnnouncement(projectId: string, announcement: AnnouncementFormData): Promise<void> {
    await axios.post(`${this.baseUrl}/projects/${projectId}/announcements`, announcement);
  }

  async changeSubscriptionPreference(projectId: number, status: SubscriptionStatus): Promise<void> {
    await axios.post(`${this.baseUrl}/account/subscriptions/${projectId}`, { status });
  }

  async startPhoneVerification(phoneNumber: string): Promise<string> {
    return (await axios.post(`${this.baseUrl}/account/phone/start`, { phoneNumber })).data;
  }

  async verifyPhoneCode(phoneNumber: string, code: string): Promise<string> {
    return (await axios.post(`${this.baseUrl}/account/phone/verify`, { phoneNumber, code })).data;
  }

  async linkPushApiSubscription(subscription: any, displayName: string): Promise<void> {
    return (await axios.post(`${this.baseUrl}/account/pushapi/register`, { subscription, displayName })).data;
  }

  async getLinkedExternalAccounts(): Promise<GetLinkedExternalAccounts200ResponseInner[]> {
    return (await axios.get(`${this.baseUrl}/account/externalaccounts`)).data;
  }

  async getProjects(): Promise<Project[]> {
    return (await axios.get(`${this.baseUrl}/projects`)).data;
  }

  async getSubscriptions(): Promise<Subscription[]> {
    return (await axios.get(`${this.baseUrl}/account/subscriptions`)).data;
  }

  async getAccountSettings(): Promise<Record<string, string>> {
    return (await axios.get(`${this.baseUrl}/account/settings`)).data;
  }

}