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
import { Controller, useForm } from 'react-hook-form';
import { useApi } from '@/contexts/ApiProvider';
import { PublishingPermissions } from '@/lib/ryp-publishing-api';


interface AnnouncementFormProps {
  project: Project;
  onSubmit: (announcement: AnnouncementFormData) => void;
}

export default function PublishAnnouncementForm({ project, onSubmit }: AnnouncementFormProps) {
  const [content, setContent] = useState('');
  const [title, setTitle] = useState('');
  const [markdown, setMarkdown] = useState('');
  const [policies, setPolicies] = useState(project.policies.map((policy) => policy.policyId));
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
  } = useForm<AnnouncementFormData>();
  const { ref } = register('title', { required: true });

  useEffect(() => {
    const fetchPublishingPermissions = async () => {
      const permissions = await api.getPublishingPermissionsForAccount(project.id);
      setPublishingPermissions(permissions);
    }
    titleRef.current?.focus();
    fetchPublishingPermissions();
  }, [api, project.id, titleRef])

  useEffect(() => {
    setValue('policies', policies);
  }, [policies, setValue]);

  const handleOnChange = (event: React.ChangeEvent<HTMLInputElement>, onChange: (...event: any[]) => void) => {
    if (event.target.name === 'title') {
      setTitle(event.target.value);
      setMarkdown(`# ${event.target.value}\n\n${content}`);
    } else if (event.target.name === 'content') {
      setContent(event.target.value);
      setMarkdown(`# ${title}\n\n${event.target.value}`);
    }
    onChange(event);
  };

  const changePolicy = (values: any) => {
    setPolicies(values);
  };

  return (
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="5">
        <Stack spacing="5" divider={<StackDivider />}>
          <FormControl id="policies" isRequired>
            <FormLabel>{t('publish.form.policies')}</FormLabel>
            <PolicySelection project={project} onChange={changePolicy} publishingPermissions={publishingPermissions} />
          </FormControl>
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
                <Textarea
                  maxW={{ md: '100%' }}
                  rows={15}
                  resize="vertical"
                  {...register('content', { required: true })}
                />
                <FormErrorMessage>{errors.content && t('publish.form.contentError')}</FormErrorMessage>
              </Stack>
            </FormControl>
            <Box w={{ base: '100%', md: '50%' }} pl={{ base: 0, md: 5}}>
              <FormLabel>{t('publish.form.preview')}</FormLabel>
              <Markdown components={components}>{markdown}</Markdown>
            </Box>
          </Stack>

          <FormControl id="link">
            <FormLabel>{t('publish.form.link')}</FormLabel>
            <Input
              maxW={{ md: '50%' }}
              defaultValue=''
              {...register('link')}
            />
          </FormControl>

          <Button alignSelf="flex-end" onClick={handleSubmit(onSubmit)}>{t('publishAnnouncementButton')}</Button>
        </Stack>
      </Stack>
    </Container>
  );
};
