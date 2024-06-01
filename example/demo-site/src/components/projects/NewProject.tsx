import { Box, Button, Container, Stack, VStack } from '@chakra-ui/react'
import { Account } from '../../lib/ryp-subscription-api';
import ProjectTypeSelection from './ProjectTypeSelection';
import { useState } from 'react';
import ProjectConfiguration from './ProjectConfiguration';
import ProjectCategory from '@/lib/types/ProjectCategory';
import ProjectsHeader from './ProjectsHeader';
import { useApi } from '@/contexts/ApiProvider';
import { useRouter } from 'next/navigation';
import useTranslation from 'next-translate/useTranslation';
import { TokenPolicy } from '@vibrantnet/core';
import { nanoid } from 'nanoid';

type NewProjectProps = {
    account: Account;
};

export interface FormData {
  name: string
  logo: string
  url: string
  description: string
  policies: Record<string, TokenPolicy>
}


const defaultFormData: FormData = {
  name: '',
  logo: '',
  url: '',
  description: '',
  policies: { [nanoid()]: { projectName: '', policyId: '' } },
};

export default function NewProject({ account }: NewProjectProps) {
  const [projectType, setProjectType] = useState<ProjectCategory | null>(null)
  const api = useApi();
  const router = useRouter();
  const { t } = useTranslation('projects');

  const addNewProject = async (formData: FormData) => {
    const newProject = {
      name: formData.name,
      logo: formData.logo ?? '',
      url: formData.url,
      category: projectType as ProjectCategory,
      description: formData.description,
      policies: Object.values(formData.policies)
        .filter((policy) => policy.policyId.length > 0 && policy.projectName.length > 0)
        .map((policy) => ({ name: policy.projectName, policyId: policy.policyId })),
    }
    await api.addNewProject(newProject);
    router.push('/projects');
  }

  const pickType = (type: ProjectCategory) => {
    setProjectType(type);
  }

  return (
    <Box
        maxW="7xl"
        mx="auto"
        px={{ base: '4', md: '8', lg: '12' }}
        py={{ base: '6', md: '8', lg: '12' }}
    >
      <ProjectsHeader
            backButtonLink='/projects'
            backButtonText={t('backToProjectList')}
            title={t('createNewProjectTitle')}
            description={t('createNewProjectDescription')}
        />
      <Container py={{ base: '4', md: '8' }}>
        <VStack spacing="0">
          <Stack spacing="4" direction={{ base: 'row', md: 'column' }} minW="3xl">
            <Stack direction={{ base: 'column', md: 'row' }} alignContent="center" alignItems="center" justifyContent="space-between">
              <ProjectTypeSelection handleChange={(type) => pickType(type)} type={projectType} />
              {projectType && <Button aria-label={t('add.changeType')} onClick={() => setProjectType(null) } variant="text">{t('add.changeType')}</Button>}
            </Stack>
            {projectType && (<ProjectConfiguration account={account} type={projectType} formData={defaultFormData} onSubmit={addNewProject} />)}
          </Stack>
        </VStack>
      </Container>
    </Box>
  )
}