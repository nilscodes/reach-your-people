import { GridItem, SimpleGrid, SpaceProps, Text } from '@chakra-ui/react';
import ProjectCardSkeleton from '../projectcard/ProjectCardSkeleton';
import ProjectCard from '../projectcard/ProjectCard';
import useTranslation from 'next-translate/useTranslation';
import { SubscriptionsViewProps } from './types';

export default function SubscriptionsGridView(props: SubscriptionsViewProps) {
  const {
    account, projects, isProjectsLoading, subscriptions, currentCategory, ...rest
  } = props;
  const { t } = useTranslation('projects');
  return (<SimpleGrid columns={{ base: 1, lg: 2 }} spacing="4" {...rest}>
    {isProjectsLoading && projects.length === 0 && (<>
      <GridItem><ProjectCardSkeleton /></GridItem>
      <GridItem><ProjectCardSkeleton /></GridItem>
    </>)}
    {!isProjectsLoading && projects.length === 0 && (
      <GridItem colSpan={2} p="4"><Text color="fg.muted">{t('noProjectsFound')}</Text></GridItem>
    )}
    {projects.map((project) => {
        const subscription = subscriptions.find((subscription) => subscription.projectId === project.id);
        return (<GridItem key={project.id}>
          <ProjectCard account={account} project={project} subscription={subscription} currentCategory={currentCategory} />
        </GridItem>);
      })
    }
  </SimpleGrid>);
};
