import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreRedirectApi } from "@/lib/core-redirect-api";
 
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
      let referralUrl = process.env.NEXT_PUBLIC_API_URL + '/?ref=' + accountId;
      try {
        const shortUrl = await coreRedirectApi.createShortUrl({
          url: '/?ref=' + accountId,
          type: 'RYP',
          status: 'ACTIVE',
        });
        referralUrl = `${process.env.RYP_SHORT_URL}/${shortUrl.data.shortcode}`;
      }
      catch (error) {
        // TODO: log to Prometheus, since we want to know if this is failing, but we can just use the full URL in the meantime
      }
      await coreSubscriptionApi.updateAccountSetting(accountId, 'REFERRAL_URL', { name: 'REFERRAL_URL', value: referralUrl });
      res.status(200).json({ referralUrl });
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}