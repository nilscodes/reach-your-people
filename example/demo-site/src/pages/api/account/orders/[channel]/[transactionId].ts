import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../../auth/[...nextauth]";
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreBillingApi } from "@/lib/core-billing-api";
import { Bill } from "@/lib/ryp-billing-api";
 
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
    const accountId = session.userId;
    const channel = req.query.channel as string;
    const transactionId = req.query.transactionId as string;
    try {
      const billResponse = await coreBillingApi.getBillsForAccount(accountId);
      const billWithTransactionId = billResponse.data.find((bill: Bill) => bill.channel === channel && bill.transactionId === transactionId);
      if (!billWithTransactionId) {
        res.status(404).json({ error: 'Bill not found' });
        return;
      }
      res.status(200).json(billWithTransactionId);
    } catch (e: any) {
      console.log(e);
      res.status(500).json({ error: e.message });
    }
  }
}