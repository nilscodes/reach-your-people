import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { GetLinkedExternalAccounts200ResponseInnerSettingsEnum } from "@/lib/ryp-subscription-api";
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );

  if (req.method === 'PUT' && session?.userId) {
    const accountId = session.userId;
    const externalAccountId = +req.query.externalAccountId!;
    // Remove default from all external accounts except the one being updated
    const currentLinkedAccounts = await coreSubscriptionApi.getLinkedExternalAccounts(accountId);
    const updatedLinkedAccounts = [];
    for (const linkedExternalAccount of currentLinkedAccounts.data) {
      if (linkedExternalAccount.id !== externalAccountId && linkedExternalAccount.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications)) {
        // Set all non-chosen external accounts that are currently the default to not be the default any more
        const newSettings = linkedExternalAccount.settings?.filter((setting) => setting !== GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications);
        await coreSubscriptionApi.updateLinkedExternalAccount(accountId, linkedExternalAccount.externalAccount.id!, { settings: newSettings });
        updatedLinkedAccounts.push({ ...linkedExternalAccount, settings: newSettings });
      } else if (linkedExternalAccount.externalAccount.id === externalAccountId && !linkedExternalAccount.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications)) {
        // Set the chosen external account to be the default
        const newSettingsWithDefaultOn = [...(linkedExternalAccount.settings ?? []), GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications];
        await coreSubscriptionApi.updateLinkedExternalAccount(accountId, linkedExternalAccount.externalAccount.id!, { settings: newSettingsWithDefaultOn });
        updatedLinkedAccounts.push({ ...linkedExternalAccount, settings: newSettingsWithDefaultOn });
      } else {
        updatedLinkedAccounts.push(linkedExternalAccount);
      }
    }
    res.status(200).json(updatedLinkedAccounts);
  }
}