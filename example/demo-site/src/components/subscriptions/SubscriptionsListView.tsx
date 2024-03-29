import {
  Container,
  HStack,
  Link, Skeleton, Table, Tbody, Td, Text, Th, Thead, Tr, VStack, useBreakpointValue,
} from '@chakra-ui/react';
import { SubscriptionsViewProps } from './SubscriptionsGridView';
import ProjectLogo from '../projectcard/ProjectLogo';
import ProjectTag from '../projectcard/ProjectTag';
import VerifiedIcon from '../projectcard/VerifiedIcon';
import SubscriptionActions from './SubscriptionActions';
import FavoriteButton from './FavoriteButton';
import useTranslation from 'next-translate/useTranslation';

export default function SubscriptionsListView(props: SubscriptionsViewProps) {
  const {
    account,
    projects,
    subscriptions,
    isProjectsLoading,
    ...rest
  } = props;
  const colSpan = useBreakpointValue({ base: 1, md: 3, lg: 4 });
  const { t } = useTranslation('subscriptions');
  return (<Table variant="simple" whiteSpace={{ base: 'initial', lg: 'nowrap' }} {...rest}>
    <Thead>
      <Tr>
        <Th>{t('listView.headers.name')}</Th>
        <Th display={{ base: 'none', lg: 'table-cell' }}>{t('listView.headers.url')}</Th>
        <Th display={{ base: 'none', md: 'table-cell' }}>{t('listView.headers.type')}</Th>
        <Th display={{ base: 'none', md: 'table-cell' }}>{t('listView.headers.actions')}</Th>
      </Tr>
      </Thead>
      <Tbody>
      {isProjectsLoading && (<>
        <Tr><Td colSpan={colSpan}><Skeleton h="40px" /></Td></Tr>
        <Tr><Td colSpan={colSpan}><Skeleton h="40px" /></Td></Tr>
        <Tr><Td colSpan={colSpan}><Skeleton h="40px" /></Td></Tr>
        <Tr><Td colSpan={colSpan}><Skeleton h="40px" /></Td></Tr>
      </>)}
      {!isProjectsLoading && projects.map((project) => {
        const subscription = subscriptions.find((subscription) => subscription.projectId === project.id);
        return(<Tr key={project.id}>
          <Td>
            <VStack alignItems='start'>
              <HStack spacing="4">
                {account && <FavoriteButton subscription={subscription} />}
                <ProjectLogo
                  size="sm"
                  name={project.name}
                  src={project.logo}
                  hideVerified
                />
                <VerifiedIcon isVerified={project.verified} fontSize="lg" />
                <Text>{project.name}</Text>
              </HStack>
              <Container display={{ base: 'block', md: 'none' }} px="0">
                <SubscriptionActions account={account} project={project} subscription={subscription} />
              </Container>
            </VStack>
          </Td>
          <Td display={{ base: 'none', lg: 'table-cell' }}><Link href={`${project.url}`} isExternal={true} fontWeight="medium">{project.url!.replace('https://', '')}</Link></Td>
          <Td display={{ base: 'none', md: 'table-cell' }}><ProjectTag category={project.category} /></Td>
          <Td display={{ base: 'none', md: 'table-cell' }}>
            <HStack>
              <SubscriptionActions account={account} project={project} subscription={subscription} />
            </HStack>
          </Td>
        </Tr>);
      })}
      </Tbody>
  </Table>);
};
