import { MdAccountBox, MdOutlineShare } from "react-icons/md";
import { PointsClaim } from "./ryp-points-api";
import { Achievement } from "./types/Achievement";
import { GiSpikyExplosion } from "react-icons/gi";

export const iconMap = {
  signup: MdAccountBox,
  referral: MdOutlineShare,
  premiumbuy: GiSpikyExplosion,
};

export function createAchievements(pointClaims: PointsClaim[]) {
  const achievements: Achievement[] = [{
    id: 'signup',
    icon: 'signup',
    title: 'achievements.signup.title',
    description: 'achievements.signup.description',
    type: 'boolean',
    achieved: pointClaims.some((claim) => claim.category === 'signup'),
  }, {
    id: 'referral',
    icon: 'referral',
    title: 'achievements.referral.title',
    description: 'achievements.referral.description',
    type: 'numeric',
    maxPoints: 50,
    points: pointClaims.filter((claim) => claim.category === 'referral').length,
  }, {
    id: 'earlySupporter',
    icon: 'premiumbuy',
    title: 'achievements.earlySupporter.title',
    description: 'achievements.earlySupporter.description',
    type: 'boolean',
    achieved: pointClaims.some((claim) => claim.category === 'premium' && claim.createTime && new Date(claim.createTime) < new Date('2024-08-03')),
  }];
  return achievements;
}