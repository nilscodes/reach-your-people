import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { phoneVerifyApi } from '@/lib/phone-verify-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreSubscriptionApi, makeNewLinkedExternalAccountDefaultForNotificationsIfRequired } from "@/lib/core-subscription-api";
import { formatPhoneNumber } from "@/lib/phoneutil";
 
const env = process.env.NODE_ENV

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );
  const { phoneNumber, countryCode, code } = req.body;
  
  if (session?.userId) {
    const accountId = session.userId;
    if (req.method === 'POST') {
      const fullPhoneNumber = `${countryCode.replace(/[^+\d]/g, '')}${phoneNumber.replace(/[^+\d]/g, '')}`;
      const response = await checkVerification(fullPhoneNumber, code);
      if (response.data === 'approved') {
        const externalAccount = await coreSubscriptionApi.createExternalAccount({
          type: 'sms',
          referenceId: fullPhoneNumber,
          displayName: formatPhoneNumber(countryCode, phoneNumber),
        });
        await coreSubscriptionApi.linkExternalAccount(accountId, externalAccount.data.id!);
        await makeNewLinkedExternalAccountDefaultForNotificationsIfRequired(accountId, externalAccount.data.id!);
      }
      res.status(response.status).json(response.data);
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}

async function checkVerification(phoneNumber: string, code: string) {
  if (env === 'development') {
    return { status: 200, data: code === '123456' ? 'approved' : 'pending' };
  }
  return await phoneVerifyApi.post('/checkVerificationStatus', {
    phoneNumber,
    code,
  });
}
