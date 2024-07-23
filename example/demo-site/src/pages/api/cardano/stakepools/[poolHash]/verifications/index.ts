import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../../auth/[...nextauth]";
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
  
  if (req.method === 'POST' && session?.userId) {
    const poolHash = req.query.poolHash as string;
    try {
      const response = await coreVerificationApi.startStakepoolVerification(poolHash);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      res.status(error.response.status).json(error.response.data);
    }
  }
}