import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../auth/[...nextauth]";
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { ProjectNotificationSetting } from "@/lib/ryp-subscription-api";
 
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
    const projectId = Number(req.query.projectid);
    if (req.method === 'GET') {
      try {
        const response = await coreSubscriptionApi.getNotificationsSettingsForAccountAndProject(session.userId, projectId);
        res.status(response.status).json(response.data);
      } catch (error: any) {
        res.status(error.response.status).json(error.response.data);
      }
    } else if (req.method === 'PUT') {
      const notificationSettings = req.body as ProjectNotificationSetting[];
      try {
        const response = await coreSubscriptionApi.updateNotificationsSettingsForAccountAndProject(session.userId, projectId, notificationSettings);
        res.status(response.status).json(response.data);
      } catch (error: any) {
        console.log(error);
        res.status(error.response.status).json(error.response);
      }
    }
  }
}