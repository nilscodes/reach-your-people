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
  
  if (req.method === 'POST' && session?.userId) {
    const projectId = Number(req.query.projectid);
    const basicAnnouncement = req.body;
    basicAnnouncement.author = session.userId;
    const response = await corePublishingApi.publishAnnouncementForProject(projectId, basicAnnouncement, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    res.status(response.status).json(response.data);
  }
}