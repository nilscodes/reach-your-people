import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../auth/[...nextauth]";
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

  if (req.method === 'PATCH' && session?.userId) {
    const accountId = session.userId;
    const externalAccountId = +req.query.externalAccountId!;
    const update = {
      settings: req.body.settings,
    }
    const response = await coreSubscriptionApi.updateLinkedExternalAccount(accountId, externalAccountId, update);
    res.status(response.status).json(response.data);
  }
}