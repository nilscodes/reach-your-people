export type BasicAnnouncementDto = {
  id: string;
  type: AnnouncementType;
  author: number;
  title: string;
  content: string;
  link: string;
  externalLink?: string;
  metadata?: Record<string, string>;
}

export type BasicProjectDto = {
  id: number;
  name: string;
  url: string;
  logo: string;
}

export type MessageDto = {
  referenceId: string;
  referenceName: string;
  announcement: BasicAnnouncementDto;
  metadata?: string;
  project: BasicProjectDto;
  language: string;
}

export type StatisticsDto = {
  delivered?: number;
  failures?: number;
  views?: number;
}

export type StatisticsUpdateDto = {
  announcementId: string;
  statistics: StatisticsDto;
}

export enum AnnouncementType {
  STANDARD = 'STANDARD',
  TEST = 'TEST',
  GOVERNANCE_VOTE = 'GOVERNANCE_VOTE',
  STAKEPOOL_RETIREMENT = 'STAKEPOOL_RETIREMENT',
}