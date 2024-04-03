import { corePublishingApi } from '@/lib/core-publishing-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  if (req.method === 'GET') {
    const announcementId = `${req.query.announcementid}`;
    const response = await corePublishingApi.getAnnouncementById(announcementId);
    res.status(response.status).json(response.data);
  }
}