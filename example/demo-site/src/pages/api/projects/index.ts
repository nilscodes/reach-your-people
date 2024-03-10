import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../auth/[...nextauth]";
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
  
  if (req.method === 'POST' && session?.userId) {
    const accountId = session.userId;
    const project = req.body;
    const response = await coreSubscriptionApi.addNewProject(project, accountId);
    res.status(response.status).json(response.data);
  } else if (req.method === 'GET') {
    const response = await coreSubscriptionApi.listProjects();
    res.status(response.status).json(response.data);
  }
}