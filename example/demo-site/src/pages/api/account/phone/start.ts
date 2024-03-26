import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { phoneVerifyApi } from '@/lib/phone-verify-api';
import type { NextApiRequest, NextApiResponse } from 'next'
 
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
  const { phoneNumber } = req.body;
  
  if (session?.userId) {
    if (req.method === 'POST') {
      if (env === 'development') {
        res.status(204).end();
      } else {
        const response = await phoneVerifyApi.post('/startVerification', {
          phoneNumber,
          channel: 'sms',
        });
        res.status(response.status).end();
      }
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}