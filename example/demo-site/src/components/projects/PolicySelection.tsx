import { Box, HStack, Spinner, Stack, Text } from '@chakra-ui/react'
import { CheckboxCard, CheckboxCardGroup } from '../CheckboxCardGroup'
import { Policy, Project } from '@/lib/types/Project';
import { FormEventHandler } from 'react';
import VerifiedIcon from '../projectcard/VerifiedIcon';
import { getPolicyIdDisplayName } from '@/lib/cardanoutil';
import { PolicyPublishingPermission, PolicyPublishingPermissionPermissionEnum, PublishingPermissions } from '@/lib/ryp-publishing-api';
import useTranslation from 'next-translate/useTranslation';

interface PolicySelectionProps {
  project: Project;
  publishingPermissions: PublishingPermissions | null;
  onChange: FormEventHandler<HTMLDivElement> & ((value: (string | number)[]) => void);
}

const isMatchingAndAllowedPolicy = (permission: PolicyPublishingPermission, policy: Policy) => {
  return permission.policyId === policy.policyId && [PolicyPublishingPermissionPermissionEnum.Cip66, PolicyPublishingPermissionPermissionEnum.Manual].includes(permission.permission);
}

export const PolicySelection = ({ project, publishingPermissions, onChange }: PolicySelectionProps) => {
  const { t } = useTranslation('publish');
  if (publishingPermissions) {
    return (<Box as="section" maxW="md" py="4">
      <CheckboxCardGroup defaultValue={project.policies.map((policy) => policy.policyId)} spacing="3" onChange={onChange}>
        {project.policies.map((policy) => (
          <CheckboxCard key={policy.policyId} value={policy.policyId}>
            <HStack spacing="4" alignItems="flex-start">
              <VerifiedIcon isVerified={publishingPermissions.policies.some((p) => isMatchingAndAllowedPolicy(p, policy))} fontSize="lg" />
              <Box>
                <Text color="fg.emphasized" fontWeight="medium" fontSize="sm">
                  {policy.name}
                </Text>
                <Text color="fg.muted" textStyle="sm">
                  {getPolicyIdDisplayName(policy.policyId)}
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