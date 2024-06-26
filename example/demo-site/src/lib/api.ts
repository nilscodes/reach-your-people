import axios from "axios";
import { Project, ProjectCreationRequest } from "./types/Project";
import { Subscription } from "./types/Subscription";
import { SubscriptionStatus } from "./types/SubscriptionStatus";
import { AnnouncementFormData } from "@/components/projects/PublishAnnouncement";
import { GetLinkedExternalAccounts200ResponseInner, GetLinkedExternalAccounts200ResponseInnerSettingsEnum, ProjectNotificationSetting } from "./ryp-subscription-api";
import { PublishingPermissions } from "./ryp-publishing-api";

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

  async addNewProject(project: ProjectCreationRequest, logo: File | null): Promise<Project> {
    const formData = new FormData();
    formData.append('project', JSON.stringify(project));
    if (logo) {
      formData.append('logo', logo);
    }
    return (await axios.post(`${this.baseUrl}/projects`, formData)).data;
  }

  async publishAnnouncement(projectId: number, announcement: AnnouncementFormData): Promise<void> {
    await axios.post(`${this.baseUrl}/projects/${projectId}/announcements`, announcement);
  }

  async getPublishingPermissionsForAccount(projectId: number): Promise<PublishingPermissions> {
    return (await axios.get(`${this.baseUrl}/projects/${projectId}/publishingstatus`)).data;
  }

  async changeSubscriptionPreference(projectId: number, status: SubscriptionStatus): Promise<void> {
    await axios.post(`${this.baseUrl}/account/subscriptions/${projectId}`, { status });
  }

  async startPhoneVerification(phoneNumber: string): Promise<string> {
    return (await axios.post(`${this.baseUrl}/account/phone/start`, { phoneNumber })).data;
  }

  async verifyPhoneCode(countryCode: string, phoneNumber: string, code: string): Promise<string> {
    return (await axios.post(`${this.baseUrl}/account/phone/verify`, { countryCode, phoneNumber, code })).data;
  }

  async linkPushApiSubscription(subscription: any, displayName: string): Promise<void> {
    return (await axios.post(`${this.baseUrl}/account/pushapi/register`, { subscription, displayName })).data;
  }

  async getLinkedExternalAccounts(): Promise<GetLinkedExternalAccounts200ResponseInner[]> {
    return (await axios.get(`${this.baseUrl}/account/externalaccounts`)).data;
  }

  async makeDefaultForNotifications(externalAccountId: number): Promise<GetLinkedExternalAccounts200ResponseInner[]> {
    return (await axios.put(`${this.baseUrl}/account/externalaccounts/${externalAccountId}/default`)).data;
  }

  async getNotificationsSettingsForAccountAndProject(projectId: number): Promise<ProjectNotificationSetting[]> {
    return (await axios.get(`${this.baseUrl}/account/projects/${projectId}/notifications`)).data;
  }

  async updateNotificationsSettingsForAccountAndProject(projectId: number, settings: ProjectNotificationSetting[]): Promise<ProjectNotificationSetting[]> {
    return (await axios.put(`${this.baseUrl}/account/projects/${projectId}/notifications`, settings)).data;
  }

  async getProjects(): Promise<Project[]> {
    return (await axios.get(`${this.baseUrl}/projects`)).data;
  }

  async getSubscriptions(): Promise<Subscription[]> {
    return (await axios.get(`${this.baseUrl}/account/subscriptions`)).data;
  }

  async getSubscription(projectId: number): Promise<Subscription | null> {
    return (await axios.get(`${this.baseUrl}/account/subscriptions/${projectId}`)).data;
  }

  async getAccountSettings(): Promise<Record<string, string>> {
    return (await axios.get(`${this.baseUrl}/account/settings`)).data;
  }

  async updateLinkedExternalAccountSettings(externalAccountId: number, settings: GetLinkedExternalAccounts200ResponseInnerSettingsEnum[]): Promise<GetLinkedExternalAccounts200ResponseInner> {
    return (await axios.patch(`${this.baseUrl}/account/externalaccounts/${externalAccountId}/settings`, { settings })).data;
  }

  async generateReferralUrl(): Promise<string> {
    return (await axios.post(`${this.baseUrl}/account/settings/referralurl`)).data.referralUrl;
  }

  async submitReferredBy(referredBy: number): Promise<void> {
    await axios.post(`${this.baseUrl}/account/settings/referral`, { referredBy });
  }

}