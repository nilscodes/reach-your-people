import { Container } from '@chakra-ui/react'
import { Account } from '../../lib/ryp-subscription-api';
import ProjectsHeader from '../projects/ProjectsHeader';
import useTranslation from 'next-translate/useTranslation';
import { Project } from '@/lib/types/Project';
import ProjectDetails from './ProjectDetails';
import StandardContentWithHeader from '../StandardContentWithHeader';
import { useRouter } from 'next/router';
import ProjectPolicies from './ProjectPolicies';
import ProjectStakepools from './ProjectStakepools';

type ProjectPageProps = {
  account: Account | null;
  project: Project;
}

export default function ProjectPage({ account, project }: ProjectPageProps) {
  const { t } = useTranslation('projects')
  const router = useRouter();
  const previousLocation = router.query.from as string;
  let backLink = '/projects/all'
  // If previous location has dashes, it is an announcement ID, otherwise a category
  if (previousLocation && previousLocation.includes('-')) {
    backLink = `/announcements/${previousLocation}`
  } else if (previousLocation) {
    backLink = `/projects/${previousLocation}`
  }
  
  return (<StandardContentWithHeader
    header={<ProjectsHeader
      backButtonLink={backLink}
      backButtonText={t('back')}
      title={t('projectDetails')}
    />}
    px="0"
  >
    <Container py={{ base: '4', md: '8' }}>
      <ProjectDetails account={account} project={project} />
    </Container>
    {project.policies.length > 0 && <ProjectPolicies project={project} />}
    {project.stakepools.length > 0 && <ProjectStakepools project={project} />}
  </StandardContentWithHeader>);
}