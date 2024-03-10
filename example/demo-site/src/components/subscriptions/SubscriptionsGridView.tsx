import { GridItem, SimpleGrid, SpaceProps } from '@chakra-ui/react';
import { Project } from '@/lib/types/Project';
import ProjectCardSkeleton from '../projectcard/ProjectCardSkeleton';
import ProjectCard from '../projectcard/ProjectCard';
import { Subscription } from '@/lib/types/Subscription';
import { Account } from '../../lib/ryp-subscription-api';

export interface SubscriptionsViewProps extends SpaceProps {
  account: Account | null;
  projects: Project[];
  subscriptions: Subscription[];
  isProjectsLoading: boolean;
}

export default function SubscriptionsGridView(props: SubscriptionsViewProps) {
  const {
    account, projects, isProjectsLoading, subscriptions, ...rest
  } = props;
  return (<SimpleGrid columns={{ base: 1, lg: 2 }} spacing="4" {...rest}>
    {isProjectsLoading && projects.length === 0 && (<>
      <GridItem><ProjectCardSkeleton /></GridItem>
      <GridItem><ProjectCardSkeleton /></GridItem>
    </>)}
    {projects.map((project) => {
        const subscription = subscriptions.find((subscription) => subscription.projectId === project.id);
        return (<GridItem key={project.id}>
          <ProjectCard account={account} project={project} subscription={subscription} />
        </GridItem>);
      })
    }
  </SimpleGrid>);
};
