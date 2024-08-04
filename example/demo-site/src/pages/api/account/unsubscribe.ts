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

  const { email } = req.body as { email: string };
  
  if (session?.userId) {
    const accountId = session.userId;
    console.log(`Logged in account ${accountId} is unsubscribing from email with address ${email}.`);
  }

  try {
    await coreSubscriptionApi.unsubscribeFromEmail({ email });
    res.status(204).end();
  } catch (error) {
    console.error('Failed to unsubscribe from email', error);
    res.status(500).json({ message: 'Failed to unsubscribe from email' });
  }
}