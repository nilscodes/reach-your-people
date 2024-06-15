import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import type { NextApiRequest, NextApiResponse } from 'next'
import { corePointsApi } from "@/lib/core-points-api";
 
const rypTokenId = +(process.env.RYP_TOKEN_ID || 0);
const referralPoints = +(process.env.REFERRAL_POINTS || 10000);

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
      const referredBy = +req.body.referredBy;
      try {
        /*
         * This is a current workaround where we send the referral separate from the account registrations.
         * Due to the complicated nature of passing information into the NextAuth signIn callback, we are unable to pass the referral information in the initial registration.
         */
        if (rypTokenId > 0 && referredBy > 0 && referredBy !== accountId) {
          await corePointsApi.createPointClaim(referredBy, rypTokenId, `referral-${accountId}`, {
            accountId: referredBy,
            tokenId: rypTokenId,
            claimId: `referral-${accountId}`,
            category: 'referral',
            points: referralPoints,
            claimed: true,
          });
        }
      }
      catch (error: any) {
        if (!error.response || error.response.status !== 409) {
          console.error(`Failed to create referral points for ${referredBy} for ${accountId}`, error);
        }
        // TODO: log to Prometheus, since we want to know if this is failing with something else than a 409
      }
      res.status(204).end();
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}