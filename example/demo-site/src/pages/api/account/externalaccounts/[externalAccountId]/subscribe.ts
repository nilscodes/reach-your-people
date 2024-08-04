import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { AxiosError } from "axios";
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );

  if (req.method === 'POST' && session?.userId) {
    const accountId = session.userId;
    const externalAccountId = +req.query.externalAccountId!;
    const { subscribe } = req.body;
    // Remove default from all external accounts except the one being updated
    const currentLinkedAccounts = await coreSubscriptionApi.getLinkedExternalAccounts(accountId);
    const updatedLinkedAccounts = [];
    for (const linkedExternalAccount of currentLinkedAccounts.data) {
      if (linkedExternalAccount.externalAccount.id === externalAccountId) {
        try {
          const subscriptionStatusResponse = await coreSubscriptionApi.updateLinkedExternalAccountSubscriptionStatus(accountId, linkedExternalAccount.externalAccount.id!, subscribe);
          linkedExternalAccount.externalAccount.unsubscribeTime = (subscriptionStatusResponse.data) ? undefined : new Date().toISOString();
          updatedLinkedAccounts.push({ ...linkedExternalAccount });
        } catch (error: any) {
          if (error instanceof AxiosError && error.response?.status === 409) {
            res.status(409).json(error.response.data)
          }
          res.status(500).json({ error: 'Error when setting subscription status' });
          return
        }
      } else {
        updatedLinkedAccounts.push(linkedExternalAccount); // Just add unchanged linked external accounts
      }
    }
    res.status(200).json(updatedLinkedAccounts);
  }
}