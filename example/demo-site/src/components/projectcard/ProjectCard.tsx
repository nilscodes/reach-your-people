import {
  HStack,
  Icon,
  IconButton,
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
import { Account } from '../../lib/ryp-subscription-api';
import SubscriptionActions from '../subscriptions/SubscriptionActions';
import useTranslation from 'next-translate/useTranslation';
import { makeCdnUrl } from '@/lib/cdn';
import VerifiedTag from './VerifiedTag';
import NextLink from '../NextLink';
import { RiProfileLine } from 'react-icons/ri';
import { formatISODateToCustomString } from '@/lib/dateutil';

type ProjectCardProps = {
  account: Account | null;
  project: Project;
  subscription?: Subscription;
  currentCategory?: string;
};

export default function ProjectCard({ account, project, subscription, currentCategory }: ProjectCardProps) {
  const { t } = useTranslation('projects');
  const { t: tc } = useTranslation('common');
  const {
    name, tags, registrationTime,
  } = project;

  const projectIsVerified = project.manuallyVerified !== null;

  return (<Card>
    <Stack direction={{ base: 'column', md: 'row' }} spacing={{ base: '4', md: '10' }}>
      <ProjectLogo
        name={name}
        src={makeCdnUrl(project.logo)}
        isVerified={projectIsVerified}
        fontSize="2xl"
      />
      <CardContent>
        <CardHeader title={name} subscription={subscription} favoriteButton={account !== null} />
        <Link href={project.url} isExternal={true} fontWeight="medium">{project.url.replace('https://', '')}</Link>
        <Stack spacing="1" mt="2">
          {registrationTime && (<HStack fontSize="sm">
              <Icon as={GoCalendar} color="gray.500" />
              <Text>{formatISODateToCustomString(registrationTime)}</Text>
            </HStack>)}
        </Stack>

        <Text fontWeight="semibold" mt="8" mb="2">{tc('project.tags')}</Text>

        <Wrap shouldWrapChildren>
          <ProjectTag category={project.category} />
          <VerifiedTag isVerified={projectIsVerified} />
          {tags?.map((tag: string) => (<Tag key={tag}>{tag}</Tag>))}
        </Wrap>
      </CardContent>
      <Stack spacing="4" mt={{ base: '4', md: '0' }} direction={{ base: 'row', md: 'column' }}>
        <SubscriptionActions account={account} project={project} subscription={subscription} />
        <NextLink href={`/projects/${project.id}`}>
          <IconButton icon={<RiProfileLine />} variant="ghost" aria-label={t('projectDetails')} />
        </NextLink>
      </Stack>
    </Stack>
  </Card>
  );
};
