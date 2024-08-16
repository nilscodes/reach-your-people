import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { coreVerificationApi } from '@/lib/core-verification-api';
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

  if (req.method === 'GET' && session?.userId) { // In theory it doesn't require a logged in user but to reduce spam potential we ensure the user is logged in
    const stakeAddress = req.query.stakeAddress as string;
    try {
      const response = await coreVerificationApi.getDrepDetailsForStakeAddress(stakeAddress);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      res.status(error.response.status).json(error.response.data);
    }
  }
}