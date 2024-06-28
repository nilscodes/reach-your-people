import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { GetLinkedExternalAccounts200ResponseInnerRoleEnum } from "@/lib/ryp-subscription-api";
import { corePublishingApi } from "@/lib/core-publishing-api";
import { TestStatus } from "@/lib/types/TestStatus";

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
    const accountId = session.userId;
    const externalAccountId = +req.query.externalAccountId!;
    const linkedAccounts = (await coreSubscriptionApi.getLinkedExternalAccounts(accountId)).data;
    const linkedAccount = linkedAccounts.find((linkedAccount) => linkedAccount.externalAccount.id === externalAccountId && linkedAccount.role === GetLinkedExternalAccounts200ResponseInnerRoleEnum.Owner);
    if (linkedAccount !== undefined) {
      if (req.method === 'POST') {
        const testPublish = await corePublishingApi.sendTestAnnouncement(accountId, linkedAccount.externalAccount.id!);
        await coreSubscriptionApi.updateLinkedExternalAccount(accountId, linkedAccount.externalAccount.id!, { lastTested: new Date().toISOString() })
        res.status(testPublish.status).json(testPublish.data);
      } else if(req.method === 'GET') {
        const announcementId = req.query.announcementId as string;
        const announcement = (await corePublishingApi.getAnnouncementById(announcementId)).data;
        const delivered = announcement.statistics?.delivered?.[linkedAccount.externalAccount.type] ?? 0;
        const failures = announcement.statistics?.failures?.[linkedAccount.externalAccount.type] ?? 0;
        if (delivered > 0 || failures > 0) {
          res.status(200).json({ status: (delivered ? TestStatus.Delivered : TestStatus.Failed) });
        }
        res.status(200).json({ status: TestStatus.Waiting });
      }
    } else {
      res.status(404).json({ message: 'not found' });
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}