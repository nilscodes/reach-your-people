import { createNonce } from '@/lib/cardano';
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  if (req.method === 'DELETE') {
    const accountId = +req.query.accountId!;
    const externalAccountId = +req.query.externalAccountId!;
    const response = await coreSubscriptionApi.unlinkExternalAccount(accountId, externalAccountId);
    res.status(response.status).end();
  }
}