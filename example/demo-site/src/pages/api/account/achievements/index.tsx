import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import type { NextApiRequest, NextApiResponse } from 'next'
import { corePointsApi } from "@/lib/core-points-api";
import { createAchievements } from "@/lib/achievements";
 
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
    if (req.method === 'GET') {
      const pointClaims = (await corePointsApi.getPointClaimsForAccount(session.userId)).data;
      const achievements = createAchievements(pointClaims);
      res.status(200).json(achievements);
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}