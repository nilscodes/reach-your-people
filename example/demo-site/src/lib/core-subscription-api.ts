import { Configuration, DefaultApi, GetLinkedExternalAccounts200ResponseInnerSettingsEnum, LinkExternalAccount200Response } from "@/lib/ryp-subscription-api/index"

export const coreSubscriptionApi = new DefaultApi(new Configuration({
  basePath: process.env.SUBSCRIPTION_SERVICE_URL ?? "http://localhost:8071",
}));

export async function makeNewLinkedExternalAccountDefaultForNotificationsIfRequired(accountId: number, externalAccountId: number) {
  const currentLinkedAccounts = await coreSubscriptionApi.getLinkedExternalAccounts(accountId);
  const existingDefaultNotificationsAccount = currentLinkedAccounts.data.find((account) => account.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications));
  const settings = existingDefaultNotificationsAccount ? [] : [GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications];
  return coreSubscriptionApi.updateLinkedExternalAccount(accountId, externalAccountId, { settings });
}