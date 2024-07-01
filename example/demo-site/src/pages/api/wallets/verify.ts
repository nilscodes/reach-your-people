import { DataSignature } from '@meshsdk/core';
import { verifySignature, verifyTransaction } from '@/lib/cardano';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  try {
    const { signature, stakeAddress, txCbor } = req.body as { signature: DataSignature, stakeAddress: string, txCbor: string };
    if (txCbor) {
      console.log('Verifying transaction', txCbor);
      const result = verifyTransaction(txCbor);
      console.log('Verification result', result);
      return res.status(200).json(result);
    } else {
      console.log('Verifying signature', JSON.stringify(signature), stakeAddress);
      const result = await verifySignature(signature, stakeAddress);  
      console.log('Verification result', result);
      return res.status(200).json(result);
    }
  } catch (err) {
    res.status(404).json({ error: 'Could not find matching verification.' })
  }
}