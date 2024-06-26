import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { SubscriptionStatus } from "@/lib/types/SubscriptionStatus";
 
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
    const projectId = Number(req.query.projectid);
    if (req.method === 'GET') {
      const response = await coreSubscriptionApi.getAllSubscriptionsForAccount(accountId);
      const subscriptionForProject = response.data.find(subscription => subscription.projectId === projectId);
      res.status(200).json(subscriptionForProject);
    } else if (req.method === 'POST') {
      const subscriptionStatus = req.body.status as SubscriptionStatus;
      if (subscriptionStatus === SubscriptionStatus.Default) {
        const response = await coreSubscriptionApi.unsubscribeAccountFromProject(accountId, projectId);
        res.status(response.status).end();
      } else {
        const response = await coreSubscriptionApi.subscribeAccountToProject(accountId, projectId, { status: subscriptionStatus as any });
        res.status(response.status).json(response.data);
      }
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}