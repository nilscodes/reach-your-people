import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { FirstStepsItems, bitmaskToEnum, enumToBitmask } from "@/lib/types/FirstSteps";
import { corePointsApi } from "@/lib/core-points-api";
import { GetAllSubscriptionsForAccount200ResponseInner, GetAllSubscriptionsForAccount200ResponseInnerCurrentStatusEnum } from "@/lib/ryp-subscription-api";

const rypTokenId = +(process.env.RYP_TOKEN_ID || 0);

const isExplicitlySubscribedOrUnsubscribed = (subscription: GetAllSubscriptionsForAccount200ResponseInner) => {
  return subscription.currentStatus === GetAllSubscriptionsForAccount200ResponseInnerCurrentStatusEnum.Subscribed
    || subscription.currentStatus === GetAllSubscriptionsForAccount200ResponseInnerCurrentStatusEnum.Unsubscribed;
}

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );

  if (session?.userId) {
    const accountId = session.userId;
    if (req.method === 'POST') {
      const accountSettings = (await coreSubscriptionApi.getSettingsForAccount(accountId)).data;
      const firstStepsCompleted = bitmaskToEnum(+accountSettings['FIRST_STEPS'] ?? 0);
      if (req.query.update === 'true') {
        const linkedAccounts = (await coreSubscriptionApi.getLinkedExternalAccounts(accountId)).data;
        if (!firstStepsCompleted.includes(FirstStepsItems.ConnectWallet) && linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === 'cardano')) {
          firstStepsCompleted.push(FirstStepsItems.ConnectWallet);
        }
        if (!firstStepsCompleted.includes(FirstStepsItems.ConnectNotification) && linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type !== 'cardano')) {
          firstStepsCompleted.push(FirstStepsItems.ConnectNotification);
        }
        if (!firstStepsCompleted.includes(FirstStepsItems.ReferFriend)) {
          try {
            const pointClaims = (await corePointsApi.getPointClaimsForAccountAndToken(accountId, rypTokenId)).data;
            if (pointClaims.some((pointClaim) => pointClaim.category === 'referral')) {
              firstStepsCompleted.push(FirstStepsItems.ReferFriend)
            }
          } catch (error) {
            // TODO Ignoring this as it should not hold of using the first steps area, but should be pushed to Prometheus since it means there's an issue
            console.error(error);
          }
        }
        if (!firstStepsCompleted.includes(FirstStepsItems.SubscribeExplicitly)) {
          const subscriptions = (await coreSubscriptionApi.getAllSubscriptionsForAccount(accountId)).data;
          if (subscriptions.some((subscription) => isExplicitlySubscribedOrUnsubscribed(subscription))) {
            firstStepsCompleted.push(FirstStepsItems.SubscribeExplicitly);
          }
        }
      } else {
        if (firstStepsCompleted.includes(FirstStepsItems.ConnectNotification)
          && firstStepsCompleted.includes(FirstStepsItems.ConnectWallet)
          && firstStepsCompleted.includes(FirstStepsItems.SubscribeExplicitly)
          && firstStepsCompleted.includes(FirstStepsItems.ReferFriend)) {
          firstStepsCompleted.push(FirstStepsItems.Completed);
        } else {
          firstStepsCompleted.push(FirstStepsItems.Cancelled);
        }
      }
      const newBitmask = enumToBitmask(firstStepsCompleted);
      const response = await coreSubscriptionApi.updateAccountSetting(accountId, 'FIRST_STEPS', { name: 'FIRST_STEPS', value: newBitmask.toString() });
      res.status(response.status).json(response.data);
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}