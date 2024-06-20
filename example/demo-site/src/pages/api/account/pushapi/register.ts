import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreSubscriptionApi, makeNewLinkedExternalAccountDefaultForNotificationsIfRequired } from "@/lib/core-subscription-api";
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );
  const { subscription, displayName } = req.body;
  
  if (session?.userId) {
    const accountId = session.userId;
    if (req.method === 'POST') {
      const base64EncodedSubscription = Buffer.from(JSON.stringify(subscription)).toString('base64');
      const externalAccount = await coreSubscriptionApi.createExternalAccount({
        type: 'pushapi',
        referenceId: `${accountId}`,
        displayName,
        metadata: base64EncodedSubscription,
      });
      await coreSubscriptionApi.linkExternalAccount(accountId, externalAccount.data.id!);
      const response = await makeNewLinkedExternalAccountDefaultForNotificationsIfRequired(accountId, externalAccount.data.id!);
      res.status(response.status).json(response.data);
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}


