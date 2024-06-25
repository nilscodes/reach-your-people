import { Container, HStack, Img, Text } from '@chakra-ui/react'
import { Account, ListProjects200Response } from '../../lib/ryp-subscription-api';
import ProjectsHeader from './ProjectsHeader';
import useTranslation from 'next-translate/useTranslation';
import { makeCdnUrl } from '@/lib/cdn';
import StandardContentWithHeader from '../StandardContentWithHeader';
import { Announcement } from '@/lib/ryp-publishing-api';
import AnnouncementStatistics from './AnnouncementStatistics';

type ViewAnnouncementStatisticsProps = {
  announcement: Announcement;
  project: ListProjects200Response;
  author: Account;
  views: number;
};

export default function ViewAnnouncementStatistics({ project, announcement, author, views }: ViewAnnouncementStatisticsProps) {
  const { t } = useTranslation('publish');

  return (
    <StandardContentWithHeader
      header={<ProjectsHeader
        backButtonLink={`/publish/${project.id}/announcements`}
        backButtonText={t('backToAnnouncementList')}
        title={t('viewAnnouncementStatisticsTitle', { announcementTitle: (announcement.announcement as any).object.summary })}
        description={t('viewAnnouncementStatisticsDescription')}
      >
        <HStack spacing="6">
          <Text fontSize="xl">{project.name}</Text>
          <Img src={makeCdnUrl(project.logo)} alt={project.name} h="3em" borderRadius="lg" />
        </HStack>
      </ProjectsHeader>}
      px="0"
    >
      <Container py={{ base: '4', md: '8' }} >
        <AnnouncementStatistics announcement={announcement} project={project} author={author} views={views} />
      </Container>
    </StandardContentWithHeader>
  )
}