import { AnnouncementType, BasicAnnouncementDto } from "./types/announcements";
import { t } from "./translations";

function makeGovernanceVoteAnnouncement(announcement: BasicAnnouncementDto, lang: string): BasicAnnouncementDto {
  return {
    ...announcement,
    title: `Event: ${announcement.type}`,
    content: `Event: ${announcement.metadata?.drepId}`,
  };
}

function makeStakepoolRetirementAnnouncement(announcement: BasicAnnouncementDto, lang: string): BasicAnnouncementDto {
  const poolNameOrHash = announcement.metadata?.poolName || announcement.metadata?.poolHash;
  const optionalTicker = announcement.metadata?.poolTicker ? ` (${announcement.metadata?.poolTicker})` : '';
  return {
    ...announcement,
    title: t('retirement.title', lang, { ns: 'stakepool-cardano' }),
    content: t('retirement.message', lang, { ns: 'stakepool-cardano', poolNameOrHash, optionalTicker }),
    link: `https://cardanoscan.io/transaction/${announcement.metadata?.transactionHash}`,
  };
}

export function createAnnouncementFromEvent(announcement: BasicAnnouncementDto, lang: string): BasicAnnouncementDto {
  if (announcement.type === AnnouncementType.GOVERNANCE_VOTE) {
    return makeGovernanceVoteAnnouncement(announcement, lang);
  // eslint-disable-next-line no-else-return
  } else if (announcement.type === AnnouncementType.STAKEPOOL_RETIREMENT) {
    return makeStakepoolRetirementAnnouncement(announcement, lang);
  }
  return announcement;
}
