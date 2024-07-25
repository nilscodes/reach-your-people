import React, { FormEventHandler, useEffect, useRef, useState } from 'react';
import {
  Box,
  Button,
  Container,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Input,
  Stack,
  StackDivider,
  Textarea,
} from '@chakra-ui/react';
import { AnnouncementFormData } from './PublishAnnouncement';
import Markdown from 'react-markdown';
import useTranslation from 'next-translate/useTranslation';
import { components } from '../chakraMarkdownComponents';
import { Project } from '@/lib/types/Project';
import { PolicySelection } from './PolicySelection';
import { Controller, set, useForm } from 'react-hook-form';
import { useApi } from '@/contexts/ApiProvider';
import { PublishingPermissions } from '@/lib/ryp-publishing-api';
import { StakepoolSelection } from './StakepoolSelection';


interface AnnouncementFormProps {
  project: Project;
  onSubmit: (announcement: AnnouncementFormData) => void;
}

export default function PublishAnnouncementForm({ project, onSubmit }: AnnouncementFormProps) {
  const [content, setContent] = useState('');
  const [title, setTitle] = useState('');
  const [markdown, setMarkdown] = useState('');
  const [publishingPermissions, setPublishingPermissions] = useState<PublishingPermissions | null>(null);
  const titleRef = useRef<HTMLInputElement | null>(null);
  const api = useApi();
  const { t } = useTranslation('publish');
  const {
    control,
    register,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<AnnouncementFormData>({
    defaultValues: {
      policies: project.policies.map((policy) => policy.policyId),
      stakepools: project.stakepools.map((stakepool) => stakepool.poolHash),
    },
  });
  const { ref } = register('title', { required: true });
  register('policies', { required: project.stakepools.length === 0 });
  register('stakepools', { required: project.policies.length === 0 });

  useEffect(() => {
    const fetchPublishingPermissions = async () => {
      const permissions = await api.getPublishingPermissionsForAccount(project.id);
      setPublishingPermissions(permissions);
    }
    titleRef.current?.focus();
    fetchPublishingPermissions();
  }, [api, project.id, titleRef])

  const handleOnChange = (event: React.ChangeEvent<HTMLInputElement> | React.ChangeEvent<HTMLTextAreaElement>, onChange: (...event: any[]) => void) => {
    if (event.target.name === 'title') {
      setTitle(event.target.value);
      setMarkdown(`# ${event.target.value}\n\n${content}`);
    } else if (event.target.name === 'content') {
      setContent(event.target.value);
      setMarkdown(`# ${title}\n\n${event.target.value}`);
    }
    onChange(event);
  };

  const changePolicies = (values: any) => {
    setValue('policies', values);
  };

  const changeStakepools = (values: any) => {
    setValue('stakepools', values);
  };

  // For later, if we want to validate that at least one policy or stakepool is selected
  // const watchPolicies = watch('policies');
  // const watchStakepools = watch('stakepools');

  // const validateAtLeastOneSelection = () => {
  //   if (watchPolicies.length === 0 && watchStakepools.length === 0) {
  //     setError('policies', { type: 'manual', message: 'At least one policy or stakepool must be selected' });
  //   } else {
  //     clearErrors('policies');
  //   }
  // };

  return (
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="5">
        <Stack spacing="5" divider={<StackDivider />}>
          {project.policies.length > 0 && (<FormControl id="policies" isRequired isInvalid={!!errors.policies}>
            <FormLabel>{t('publish.form.policies')}</FormLabel>
            <Stack w="100%">
              <PolicySelection
                project={project}
                onChange={changePolicies}
                publishingPermissions={publishingPermissions}
              />
              <FormErrorMessage>{errors.policies && t('publish.form.policiesError')}</FormErrorMessage>
            </Stack>
          </FormControl>)}
          {project.stakepools.length > 0 && (<FormControl id="stakepools" isRequired isInvalid={!!errors.stakepools}>
            <FormLabel>{t('publish.form.stakepools')}</FormLabel>
            <Stack w="100%">
              <StakepoolSelection
                project={project}
                onChange={changeStakepools}
              />
              <FormErrorMessage>{errors.stakepools && t('publish.form.stakepoolsError')}</FormErrorMessage>
            </Stack>
          </FormControl>)}
          <FormControl id="title" isRequired isInvalid={!!errors.title}>
            <FormLabel>{t('publish.form.title')}</FormLabel>
            <Stack w="100%">
              <Controller
                name="title"
                control={control}
                defaultValue={title}
                render={({ field }) => (
                  <Input
                    maxW={{ md: '50%' }}
                    {...field}
                    onChange={(event) => handleOnChange(event, field.onChange)}
                    ref={(e) => {
                      ref(e);
                      titleRef.current = e;
                    }}
                  />)}
              />
              <FormErrorMessage>{errors.title && t('publish.form.titleError')}</FormErrorMessage>
            </Stack>
          </FormControl>

          <Stack direction={{ base: 'column', md: 'row' }} spacing="4">
            <FormControl id="content" isRequired isInvalid={!!errors.content} w={{ base: '100%', md: '50%' }}>
              <FormLabel>{t('publish.form.contentWithMarkdown')}</FormLabel>
              <Stack w="100%">
                <Controller
                  name="content"
                  control={control}
                  defaultValue={content}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <Textarea
                      maxW={{ md: '100%' }}
                      rows={15}
                      resize="vertical"
                      {...field}
                      onChange={(event) => handleOnChange(event, field.onChange)}
                    />)}
                />
                <FormErrorMessage>{errors.content && t('publish.form.contentError')}</FormErrorMessage>
              </Stack>
            </FormControl>
            <Box w={{ base: '100%', md: '50%' }} pl={{ base: 0, md: 5 }}>
              <FormLabel>{t('publish.form.preview')}</FormLabel>
              <Markdown components={components}>{markdown}</Markdown>
            </Box>
          </Stack>

          <FormControl id="link">
            <FormLabel>{t('publish.form.link')}</FormLabel>
            <Input
              maxW={{ md: '50%' }}
              defaultValue=''
              {...register('externalLink')}
            />
          </FormControl>

          <Button alignSelf="flex-end" onClick={handleSubmit(onSubmit)}>{t('publishAnnouncementButton')}</Button>
        </Stack>
      </Stack>
    </Container>
  );
};
