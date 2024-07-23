import { coreVerificationApi } from '@/lib/core-verification-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  if (req.method === 'GET') {
    const poolHash = req.query.poolHash as string;
    const response = await coreVerificationApi.getStakepoolDetails(poolHash);
    res.status(response.status).json(response.data);
  }
}