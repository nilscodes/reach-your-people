import { PointsClaim } from "./ryp-points-api";
import { Achievement } from "./types/Achievement";

export function createAchievements(pointClaims: PointsClaim[]) {
  const achievements: Achievement[] = [{
    id: 'signup',
    title: 'achievements.signup.title',
    description: 'achievements.signup.description',
    type: 'boolean',
    achieved: pointClaims.some((claim) => claim.category === 'signup'),
  }, {
    id: 'referral',
    title: 'achievements.referral.title',
    description: 'achievements.referral.description',
    type: 'numeric',
    maxPoints: 50,
    points: pointClaims.filter((claim) => claim.category === 'referral').length,
  }];
  return achievements;
}