import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreBillingApi } from "@/lib/core-billing-api";
 
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
    const orderItem = req.body.orderItem;
    const transactionId = req.body.transactionId;
    try {
      const cost = orderItem === 'month' ? 15000000 : 150000000;
      const count = orderItem === 'month' ? 1 : 12;
      const newBill = {
        id: 0,
        createTime: '',
        channel: 'cardano',
        currencyId: 1,
        amountRequested: cost,
        transactionId,
        order: {
          id: 0,
          items: [{
            'type': 'premium',
            'amount': count,
          }]
        }
      }
      const billResponse = await coreBillingApi.createBill(accountId, newBill);
      res.status(billResponse.status).json(billResponse.data);
    } catch (e: any) {
      console.log(e);
      res.status(500).json({ error: e.message });
    }
  }
}