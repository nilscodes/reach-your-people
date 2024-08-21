import { createAnnouncementFromEvent } from "./events";
import { AnnouncementType, BasicAnnouncementDto } from "./types/announcements";

export default function augmentAnnouncementIfRequired(announcement: BasicAnnouncementDto, lang: string): BasicAnnouncementDto {
  if (announcement.type !== AnnouncementType.STANDARD && announcement.type !== AnnouncementType.TEST) {
    return createAnnouncementFromEvent(announcement, lang);
  }
  return announcement;
  
}