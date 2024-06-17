import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { corePublishingApi } from '@/lib/core-publishing-api';
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
  
  if (req.method === 'GET' && session?.userId) {
    const projectId = Number(req.query.projectid);
    try {
      const response = await corePublishingApi.getPublishingPermissionsForAccount(projectId, session.userId);
      res.status(response.status).json(response.data);
    } catch (error: any) {
      res.status(error.response.status).json(error.response.data);
    }
  }
}