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
import { DRepDetails } from '@/lib/ryp-verification-api';

export default function ProjectDReps({ project }: { project: Project }) {
  const { t } = useTranslation('projects');
  const [dRepDetails, setDRepDetails] = useState<Record<string, DRepDetails>>({});
  const toast = useToast();
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

  return (<Box as="section" pt={{ base: '4', md: '8' }} pb={{ base: '12', md: '12' }}>
    <Container px={0}>
      <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
        <Stack spacing="4" justify="space-between" direction={{ base: 'column', md: 'row' }}>
          <Text textStyle="lg" fontWeight="medium" flexGrow="1">
            {t('projectDReps')}
          </Text>
        </Stack>
      </Box>
    </Container>
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="6">
        {!dRepsLoaded && <Spinner />}
        {dRepsLoaded && project.dreps.map((dRep) => (<Stack key={dRep.drepId}>
          <HStack>
            <Text fontSize="lg">{t('dRepName', dRepDetails[dRep.drepId])}</Text>
            <VerifiedIcon isVerified />
          </HStack>
          <HStack spacing="3">
            <Text color="fg.muted" wordBreak={{ base: 'break-all', md: 'normal' }}>{dRep.drepId}</Text>
            <IconButton aria-label={t('copyDRepId')} icon={<Icon as={FaCopy} />} size="xs" variant="ghost" color="fg.muted" onClick={() => {
              navigator.clipboard.writeText(dRep.drepId);
              toast({
                title: t('dRepIdCopied', dRepDetails[dRep.drepId]),
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