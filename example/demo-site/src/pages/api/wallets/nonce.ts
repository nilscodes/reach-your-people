import { createNonce } from '@/lib/cardano';
import type { NextApiRequest, NextApiResponse } from 'next'
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  try {
    const { userAddress, stakeAddress } = req.body;
    const nonce = await createNonce(userAddress, stakeAddress);
    console.log('Generated nonce', nonce);
    return res.status(200).json({ nonce });
  } catch (err) {
    res.status(500).json({ error: 'failed to load data' })
  }
}