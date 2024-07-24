import { Project } from '@/lib/types/Project';
import {
  Box, Container, HStack, Icon, IconButton, Spinner, Stack, Text,
  useToast,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import { FaCopy } from 'react-icons/fa';
import VerifiedIcon from '../projectcard/VerifiedIcon';
import { useEffect, useState } from 'react';
import { useApi } from '@/contexts/ApiProvider';
import { StakepoolDetails } from '@/lib/types/StakepoolDetails';

export default function ProjectStakepools({ project }: { project: Project }) {
  const { t } = useTranslation('projects');
  const [poolDetails, setPoolDetails] = useState<Record<string, StakepoolDetails>>({});
  const toast = useToast();
  const api = useApi();

  useEffect(() => {
    const fetchPoolDetails = async () => {
      try {
        const responses = await Promise.all(
          project.stakepools.map((stakepool) => api.getStakepoolDetails(stakepool.poolHash))
        );
        const data = responses.reduce((acc, response) => {
          acc[response.poolHash] = response;
          return acc;
        }, {} as Record<string, StakepoolDetails>);
        setPoolDetails(data);
      } catch (error) {
        console.error('Error fetching pool details:', error);
      } finally {
        // setLoading(false);
      }
    };

    fetchPoolDetails();
  }, [api, project.stakepools]);

  const poolsLoaded = Object.keys(poolDetails).length === project.stakepools.length;

  return (<Box as="section" pt={{ base: '4', md: '8' }} pb={{ base: '12', md: '12' }}>
    <Container px={0}>
      <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
        <Stack spacing="4" justify="space-between" direction={{ base: 'column', md: 'row' }}>
          <Text textStyle="lg" fontWeight="medium" flexGrow="1">
            {t('projectStakepools')}
          </Text>
        </Stack>
      </Box>
    </Container>
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="6">
        {!poolsLoaded && <Spinner />}
        {poolsLoaded && project.stakepools.map((stakepool) => (<Stack key={stakepool.poolHash}>
          <HStack>
            <Text fontSize="lg">{t('poolName', poolDetails[stakepool.poolHash])}</Text>
            <VerifiedIcon isVerified />
          </HStack>
          <HStack spacing="3">
            <Text color="fg.muted" wordBreak={{ base: 'break-all', md: 'normal' }}>{stakepool.poolHash}</Text>
            <IconButton aria-label={t('copyStakepoolHash')} icon={<Icon as={FaCopy} />} size="xs" variant="ghost" color="fg.muted" onClick={() => {
              navigator.clipboard.writeText(stakepool.poolHash);
              toast({
                title: t('stakepoolHashCopied', poolDetails[stakepool.poolHash]),
                status: 'success',
                duration: 2000,
              })
            }} />
          </HStack>
        </Stack>
        ))}
      </Stack>
    </Container>
  </Box>);
}