import {
  Box,
  Button,
  Heading,
  HStack,
  Icon,
  Link,
  Skeleton,
  Stack,
  Tag,
  Text,
  useColorModeValue,
  Wrap,
} from '@chakra-ui/react'
import { FiHeart } from 'react-icons/fi'
import { Gallery } from '../projects/Gallery'
import { Account } from '@/lib/ryp-subscription-api'
import { Project } from '@/lib/types/Project'
import { makeCdnUrl } from '@/lib/cdn'
import useTranslation from 'next-translate/useTranslation'
import ProjectNotificationSettingsButton from './ProjectNotificationSettingsButton'
import { useApi } from '@/contexts/ApiProvider'
import { SubscriptionStatus } from '@/lib/types/SubscriptionStatus'
import { useEffect, useState } from 'react'
import { Subscription } from '@/lib/types/Subscription'
import SubscriptionStatusButton from './SubscriptionStatusButton'
import VerifiedTag from '../projectcard/VerifiedTag'
import ProjectTag from '../projectcard/ProjectTag'
import { GoCalendar, GoGlobe } from 'react-icons/go'
import SignInToSubscribeButton from '../SignInToSubscribeButton'
import { formatISODateToCustomString } from '@/lib/dateutil'
// import { Rating } from './Rating'

type ProjectDetailsProps = {
  account: Account | null;
  project: Project;
}

export default function ProjectDetails({ account, project }: ProjectDetailsProps) {
  const { t } = useTranslation('projects')
  const { t: tc } = useTranslation('common')
  const [subscription, setSubscription] = useState<Subscription | undefined>(undefined);
  const [subscriptionLoaded, setSubscriptionLoaded] = useState(false);
  const api = useApi();
  const images = [{
    id: '01',
    src: makeCdnUrl(project.logo),
    alt: t('projectLogoAlt', { projectName: project.name }),
  }]

  const changeSubscriptionPreference = async (status: SubscriptionStatus) => {
    await api.changeSubscriptionPreference(project.id, status);
    // TODO Could we skip the second call by returning the subscriptions in the update call above?
    setSubscription(await api.getSubscription(project.id) || undefined);
  };

  useEffect(() => {
    const getSubscription = async () => {
      if (account) {
        const subscription = await api.getSubscription(project.id);
        setSubscription(subscription || undefined);
      }
      setSubscriptionLoaded(true);
    }
    getSubscription();
  }, [api, account, project.id])

  const projectIsVerified = project.manuallyVerified !== null;

  return (
      <Stack
        direction={{ base: 'column-reverse', lg: 'row' }}
        spacing={{ base: '6', lg: '12', xl: '16' }}
      >
        <Stack spacing={{ base: '6', lg: '8' }} maxW={{ lg: 'lg' }} justify="center" flex="2">
          <Stack spacing={{ base: '3', md: '4' }}>
            <Stack spacing="3">
              {/* <HStack alignSelf="baseline">
                <Rating defaultValue={4} size="sm" />
                <Link
                  href="#"
                  fontSize="sm"
                  fontWeight="medium"
                  color={useColorModeValue('gray.600', 'gray.400')}
                >
                  12 Reviews
                </Link>
              </HStack> */}
              <Stack spacing="1" mb="3">
                <Heading size="lg" fontWeight="medium">
                  {project.name}
                </Heading>
                <Box>
                  <VerifiedTag isVerified={projectIsVerified} />
                </Box>
              </Stack>
            </Stack>
            {/* <PriceTag price={229} currency="GBP" rootProps={{ fontSize: 'xl' }} /> */}
            <Text color={useColorModeValue('gray.600', 'gray.400')}>
              {project.description}
            </Text>
          </Stack>
          <Stack direction={{ base: 'column', md: 'row' }} spacing={{ base: '6', md: '8' }}>
            <Stack flex="1">
              <HStack spacing="1" color={useColorModeValue('gray.600', 'gray.400')}>
                <Icon as={GoCalendar} boxSize="5" />
                <Text fontSize="sm" fontWeight="medium">
                  {tc('project.joined', { joinDate: formatISODateToCustomString(project.registrationTime) })}
                </Text>
              </HStack>
            </Stack>
            <Stack flex="1">
              <HStack spacing="1" color={useColorModeValue('gray.600', 'gray.400')}>
                <Icon as={GoGlobe} boxSize="5" />
                <Link href={project.url} isExternal={true} fontSize="sm" fontWeight="medium">{project.url.replace('https://', '')}</Link>
              </HStack>
            </Stack>
          </Stack>
          <Stack spacing={{ base: '4', md: '8' }} align="flex-start" justify="space-evenly" direction={{ base: 'column', md: 'row' }}>
            <Stack flex="1">
              <Text fontWeight="semibold">{tc('project.tags')}</Text>
              <Wrap shouldWrapChildren>
                <ProjectTag category={project.category} />
                {project.tags?.map((tag: string) => (<Tag key={tag}>{tag}</Tag>))}
              </Wrap>
            </Stack>
            {subscriptionLoaded && account !== null && (<Stack flex="1" width={{ base: 'full', md: 'fit-content' }}>
              <Text fontWeight="semibold">{tc('project.actions')}</Text>
              <Button
                variant="outline"
                width="full"
                leftIcon={<Icon as={FiHeart} boxSize="4" />}
              >
                {t('favorite')}
              </Button>
              <SubscriptionStatusButton subscription={subscription} onStatusChange={changeSubscriptionPreference} fullButton />
              <ProjectNotificationSettingsButton projectId={project.id} fullButton />
            </Stack>)}
            {subscriptionLoaded && account === null && (<Stack flex="1">
              <Text fontWeight="semibold">{tc('project.actions')}</Text>
              <SignInToSubscribeButton fullButton />
            </Stack>)}
            {!subscriptionLoaded && (<Stack flex="1">
              <Text fontWeight="semibold">{tc('project.actions')}</Text>
              <Skeleton height="50" />
            </Stack>)}
          </Stack>
        </Stack>
        <Gallery rootProps={{ overflow: 'hidden', flex: '3' }} images={images.slice(0, 5)} />
      </Stack>
  )
}