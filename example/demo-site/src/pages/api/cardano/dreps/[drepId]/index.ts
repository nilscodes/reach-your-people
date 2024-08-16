import { coreVerificationApi } from '@/lib/core-verification-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  if (req.method === 'GET') {
    const drepId = req.query.drepId as string;
    const response = await coreVerificationApi.getDRepDetails(drepId);
    res.status(response.status).json(response.data);
  }
}