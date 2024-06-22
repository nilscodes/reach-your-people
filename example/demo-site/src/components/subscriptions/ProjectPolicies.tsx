import { Project } from '@/lib/types/Project';
import {
  Box, Container, HStack, Icon, IconButton, Stack, Text,
  useToast,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import { FaCopy } from 'react-icons/fa';
import VerifiedIcon from '../projectcard/VerifiedIcon';

export default function ProjectPolicies({ project }: { project: Project }) {
  const { t } = useTranslation('projects');
  const toast = useToast();

  return (<Box as="section" pt={{ base: '4', md: '8' }} pb={{ base: '12', md: '12' }}>
    <Container px={0}>
      <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
        <Stack spacing="4" justify="space-between" direction={{ base: 'column', md: 'row' }}>
          <Text textStyle="lg" fontWeight="medium" flexGrow="1">
            {t('projectPolicies')}
          </Text>
        </Stack>
      </Box>
    </Container>
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="6">
        {project.policies.map((policy) => (<Stack key={policy.policyId}>
          <HStack>
            <Text fontSize="lg">{policy.name}</Text>
            <VerifiedIcon isVerified={policy.manuallyVerified !== null} />
          </HStack>
          <HStack spacing="3">
            <Text color="fg.muted" wordBreak={{ base: 'break-all', md: 'normal' }}>{policy.policyId}</Text>
            <IconButton aria-label={t('copyPolicyId')} icon={<Icon as={FaCopy} />} size="xs" variant="ghost" color="fg.muted" onClick={() => {
              navigator.clipboard.writeText(policy.policyId);
              toast({
                title: t('policyCopied', { policyName: policy.name }),
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