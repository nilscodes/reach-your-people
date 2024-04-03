import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  if (req.method === 'GET') {
    const projectId = Number(req.query.projectid);
    const response = await coreSubscriptionApi.getProject(projectId);
    res.status(response.status).json(response.data);
  }
}