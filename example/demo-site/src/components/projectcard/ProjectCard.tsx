import {
  HStack,
  Icon,
  Link,
  Stack,
  Tag,
  Text,
  Wrap,
} from '@chakra-ui/react';
import {
  GoCalendar,
} from 'react-icons/go';
import Card from './Card';
import CardContent from './CardContent';
import CardHeader from './CardHeader';
import ProjectTag from './ProjectTag';
import { Project } from '@/lib/types/Project';
import ProjectLogo from './ProjectLogo';
import { Subscription } from '@/lib/types/Subscription';
import SubscriptionStatusButton from '../subscriptions/SubscriptionStatusButton';
import { Account } from '@/lib/ryp-api';
import SignInToSubscribeButton from '../SignInToSubscribeButton';
import SubscriptionActions from '../subscriptions/SubscriptionActions';

type ProjectCardProps = {
  account: Account | null;
  project: Project;
  subscription?: Subscription;
};

function formatISODateToCustomString(isoDateString: string) {
  const date = new Date(isoDateString);
  const month = date.toLocaleString('en-US', { month: 'long' });
  const year = date.getFullYear();
  return `${month}, ${year}`;
}

export default function ProjectCard({ account, project, subscription }: ProjectCardProps) {
  const {
    name, tags, registrationTime,
  } = project;
  const url = 'url' in project ? project.url! : '';
  const verified = 'verified' in project ? project.verified : false;
  const category = 'category' in project ? project.category : undefined;
  return (<Card>
    <Stack direction={{ base: 'column', md: 'row' }} spacing={{ base: '4', md: '10' }}>
      <ProjectLogo
        name={name}
        src={project.logo}
        isVerified={verified}
        fontSize="2xl"
      />
      <CardContent>
        <CardHeader title={name} subscription={subscription} favoriteButton={account !== null} />
        <Link href={`${url}`} isExternal={true} fontWeight="medium">{url.replace('https://', '')}</Link>
        <Stack spacing="1" mt="2">
          {registrationTime && (<HStack fontSize="sm">
              <Icon as={GoCalendar} color="gray.500" />
              <Text>{formatISODateToCustomString(registrationTime)}</Text>
            </HStack>)}
        </Stack>

        <Text fontWeight="semibold" mt="8" mb="2">Tags</Text>

        <Wrap shouldWrapChildren>
          {category !== undefined && <ProjectTag category={category} />}
          {tags?.map((tag: string) => (<Tag key={tag}>{tag}</Tag>))}
        </Wrap>
      </CardContent>
      <Stack spacing="4" mt={{ base: '4', md: '0' }} direction={{ base: 'row', md: 'column' }}>
        <SubscriptionActions account={account} project={project} subscription={subscription} />
      </Stack>
    </Stack>
  </Card>
  );
};
