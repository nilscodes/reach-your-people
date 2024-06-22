import { Container, Stack, Text, Button } from '@chakra-ui/react'
import ProjectCard from './ProjectCard'
import ProjectGrid from './ProjectGrid'
import { useApi } from '@/contexts/ApiProvider';
import { Project } from '@/lib/types/Project';
import { useEffect, useState } from 'react';
import { Account } from '../../lib/ryp-subscription-api';
import NextLink from '../NextLink';
import ProjectsHeader from './ProjectsHeader';
import useTranslation from 'next-translate/useTranslation';
import StandardContentWithHeader from '../StandardContentWithHeader';

type ProjectsHomepageProps = {
  account: Account;
};

export default function ProjectsHomepage(props: ProjectsHomepageProps) {
  const api = useApi();
  const [projects, setProjects] = useState<Project[]>([]);
  const [isProjectsLoading, setIsProjectsLoading] = useState(true);
  const { t } = useTranslation('publish');

  useEffect(() => {
    api.getProjectsForAccount().then((projects) => {
      setProjects(projects);
      setIsProjectsLoading(false);
    });
  }, [api]);

  const isFirstProject = projects.length === 0;

  return (<StandardContentWithHeader
    header={<ProjectsHeader
      title={t('projectsTitle')}
      description={t('projectsDescription')}
    >
      <NextLink href="/publish/new">
        <Button variant={isFirstProject && !isProjectsLoading ? 'solid' : 'outline'}>{t('addNewProjectButton')}</Button>
      </NextLink>
    </ProjectsHeader>}
    px="0"
  >
    {isFirstProject && !isProjectsLoading && <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="4" direction={{ base: 'row', md: 'column' }}>
        <Text textStyle="lg" fontWeight="medium">{t('noProjects')}</Text>
        <Text textStyle="sm" color="fg.muted">{t('noProjectsCta')}</Text>
      </Stack>
    </Container>}
    {!isFirstProject && <ProjectGrid>
      {projects.map((project) => (
        <ProjectCard key={project.id} project={project} />
      ))}
    </ProjectGrid>}
  </StandardContentWithHeader>);
}