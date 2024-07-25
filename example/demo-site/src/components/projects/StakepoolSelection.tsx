import { Box, HStack, Spinner, Stack, Text } from '@chakra-ui/react'
import { CheckboxCard, CheckboxCardGroup } from '../CheckboxCardGroup'
import { Project } from '@/lib/types/Project';
import { FormEventHandler, useEffect, useState } from 'react';
import VerifiedIcon from '../projectcard/VerifiedIcon';
import { getStakepoolHashDisplayName } from '@/lib/cardanoutil';
import useTranslation from 'next-translate/useTranslation';
import { useApi } from '@/contexts/ApiProvider';
import { StakepoolDetails } from '@/lib/types/StakepoolDetails';

interface StakepoolSelectionProps {
  project: Project;
  onChange: FormEventHandler<HTMLDivElement> & ((value: (string | number)[]) => void);
}

export const StakepoolSelection = ({ project, onChange }: StakepoolSelectionProps) => {
  const { t } = useTranslation('publish');
  const [poolDetails, setPoolDetails] = useState<Record<string, StakepoolDetails>>({});
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
      }
    };

    fetchPoolDetails();
  }, [api, project.stakepools]);

  const poolsLoaded = Object.keys(poolDetails).length === project.stakepools.length;

  if (poolsLoaded) {
    return (<Box as="section" maxW="md" py="4">
      <CheckboxCardGroup defaultValue={project.stakepools.map((stakepool) => stakepool.poolHash)} spacing="3" onChange={onChange}>
        {project.stakepools.map((stakepool) => (
          <CheckboxCard key={stakepool.poolHash} value={stakepool.poolHash}>
            <HStack spacing="4" alignItems="flex-start">
              <VerifiedIcon isVerified fontSize="lg" />
              <Box>
                <Text color="fg.emphasized" fontWeight="medium" fontSize="sm">
                  {t('publish.form.poolName', poolDetails[stakepool.poolHash])}
                </Text>
                <Text color="fg.muted" textStyle="sm">
                  {getStakepoolHashDisplayName(stakepool.poolHash)}
                </Text>
              </Box>
            </HStack>
          </CheckboxCard>
        ))}
      </CheckboxCardGroup>
    </Box>)
  } else {
    return (<Box as="section" maxW="md" py="4">
      <Stack spacing="4" direction='row' alignItems="center">
        <Spinner size='lg' />
        <Text color="fg.muted" textStyle="sm" display={{ base: 'none', md: 'block' }}>
          {t('loadingPermissions')}
        </Text>
      </Stack>
    </Box>)
  }
}