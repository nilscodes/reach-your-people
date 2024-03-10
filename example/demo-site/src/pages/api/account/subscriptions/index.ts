import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
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
    if (req.method === 'GET') {
      const response = await coreSubscriptionApi.getAllSubscriptionsForAccount(accountId);
      res.status(response.status).json(response.data);
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}