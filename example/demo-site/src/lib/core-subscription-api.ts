import { Configuration, CreateExternalAccountRequest, DefaultApi, GetLinkedExternalAccounts200ResponseInner, GetLinkedExternalAccounts200ResponseInnerSettingsEnum, LinkExternalAccount200Response } from "@/lib/ryp-subscription-api/index"
import { isCapableOfReceivingNotifications } from "./providerutil";

const apiKey = process.env.IO_VIBRANTNET_RYP_SECURITY_APIKEY;

export const coreSubscriptionApi = new DefaultApi(new Configuration({
  basePath: process.env.SUBSCRIPTION_SERVICE_URL ?? "http://localhost:8071",
  baseOptions: apiKey ? {
    headers: {
      "Authorization": `${apiKey}`,
    },
  } : {},
}));

export async function makeNewLinkedExternalAccountDefaultForNotificationsIfRequired(accountId: number, externalAccountId: number) {
  const currentLinkedAccounts = await coreSubscriptionApi.getLinkedExternalAccounts(accountId);
  const existingDefaultNotificationsAccount = currentLinkedAccounts.data.find((account) => account.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications));
  const settings = existingDefaultNotificationsAccount ? [] : [GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications];
  return coreSubscriptionApi.updateLinkedExternalAccount(accountId, externalAccountId, { settings });
}

// TODO Might be possible to consolidate this with makeNewLinkedExternalAccountDefaultForNotificationsIfRequired
export async function makeDefaultNotificationsAccountIfNecessary(
  existingLinkedAccounts: GetLinkedExternalAccounts200ResponseInner[],
  externalAccount: CreateExternalAccountRequest,
  linkedExternalAccount: LinkExternalAccount200Response,
  accountId: number
) {
  const hasDefaultNotificationsAccount = existingLinkedAccounts.some((linkedAccount) => linkedAccount.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications));
  if (!hasDefaultNotificationsAccount && isCapableOfReceivingNotifications(externalAccount.type)) {
    const newSettingsWithDefaultOn = [...(linkedExternalAccount.settings ?? []), GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications];
    await coreSubscriptionApi.updateLinkedExternalAccount(accountId, linkedExternalAccount.externalAccount.id!, { settings: newSettingsWithDefaultOn });
  }
}

