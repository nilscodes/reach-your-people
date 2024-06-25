import { Container, HStack, Img, Text } from '@chakra-ui/react'
import { Account } from '../../lib/ryp-subscription-api';
import ProjectsHeader from './ProjectsHeader';
import useTranslation from 'next-translate/useTranslation';
import { Project } from '@/lib/types/Project';
import { makeCdnUrl } from '@/lib/cdn';
import StandardContentWithHeader from '../StandardContentWithHeader';
import { Announcement } from '@/lib/ryp-publishing-api';
import { ProjectAnnouncementTable } from './ProjectAnnouncementTable';

type ProjectAnnouncementListProps = {
  account: Account;
  project: Project;
  announcements: Announcement[];
  authors: Record<number, Account>;
};

export default function ProjectAnnouncementList({ project, announcements, authors }: ProjectAnnouncementListProps) {
  const { t } = useTranslation('publish');

  return (
    <StandardContentWithHeader
      header={<ProjectsHeader
        backButtonLink='/publish'
        backButtonText={t('backToProjectList')}
        title={t('viewAnnouncementsTitle')}
        description={t('viewAnnouncementsDescription')}
      >
        <HStack spacing="6">
          <Text fontSize="xl">{project.name}</Text>
          <Img src={makeCdnUrl(project.logo)} alt={project.name} h="3em" borderRadius="lg" />
        </HStack>
      </ProjectsHeader>}
      px="0"
    >
      <Container py={{ base: '4', md: '8' }} >
        <ProjectAnnouncementTable projectId={project.id} announcements={announcements} authors={authors} />
      </Container>
    </StandardContentWithHeader>
  )
}