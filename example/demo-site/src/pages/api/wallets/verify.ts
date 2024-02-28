import { DataSignature } from '@meshsdk/core';
import { verifySignature } from '@/lib/cardano';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  try {
    const { signature, stakeAddress } = req.body as { signature: DataSignature, stakeAddress: string };
    const result = verifySignature(signature, stakeAddress);  
    return res.status(200).json(result);
  } catch (err) {
    res.status(404).json({ error: 'Could not find matching verification.' })
  }
}