import { Box, HStack, Spinner, Stack, Text } from '@chakra-ui/react'
import { CheckboxCard, CheckboxCardGroup } from '../CheckboxCardGroup'
import { Project } from '@/lib/types/Project';
import { FormEventHandler, useEffect, useState } from 'react';
import VerifiedIcon from '../projectcard/VerifiedIcon';
import { getDRepIdDisplayName } from '@/lib/cardanoutil';
import useTranslation from 'next-translate/useTranslation';
import { useApi } from '@/contexts/ApiProvider';
import { DRepDetails } from '@/lib/ryp-verification-api';

interface DRepSelectionProps {
  project: Project;
  onChange: FormEventHandler<HTMLDivElement> & ((value: (string | number)[]) => void);
}

export const DRepSelection = ({ project, onChange }: DRepSelectionProps) => {
  const { t } = useTranslation('publish');
  const [dRepDetails, setDRepDetails] = useState<Record<string, DRepDetails>>({});
  const api = useApi();

  useEffect(() => {
    const fetchDRepDetails = async () => {
      try {
        const responses = await Promise.all(
          project.dreps.map((dRep) => api.getDRepDetails(dRep.drepId))
        );
        const data = responses.reduce((acc, response) => {
          acc[response.drepId] = response;
          return acc;
        }, {} as Record<string, DRepDetails>);
        setDRepDetails(data);
      } catch (error) {
        console.error('Error fetching dRep details:', error);
      }
    };

    fetchDRepDetails();
  }, [api, project.dreps]);

  const dRepsLoaded = Object.keys(dRepDetails).length === project.dreps.length;

  if (dRepsLoaded) {
    return (<Box as="section" maxW="md" py="4">
      <CheckboxCardGroup defaultValue={project.dreps.map((dRep) => dRep.drepId)} spacing="3" onChange={onChange}>
        {project.dreps.map((dRep) => (
          <CheckboxCard key={dRep.drepId} value={dRep.drepId}>
            <HStack spacing="4" alignItems="flex-start">
              <VerifiedIcon isVerified fontSize="lg" />
              <Box>
                <Text color="fg.emphasized" fontWeight="medium" fontSize="sm">
                  {t('publish.form.dRepName', dRepDetails[dRep.drepId])}
                </Text>
                <Text color="fg.muted" textStyle="sm">
                  {getDRepIdDisplayName(dRep.drepId)}
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