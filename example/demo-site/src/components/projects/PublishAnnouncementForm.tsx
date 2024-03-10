import React from 'react';
import {
  Box,
  Button,
  Container,
  Divider,
  FormControl,
  FormLabel,
  Input,
  Stack,
  StackDivider,
  Text,
  Textarea,
} from '@chakra-ui/react';
import { AnnouncementFormData } from './PublishAnnouncement';

interface AnnouncementFormProps {
  formData: AnnouncementFormData;
  onFormChange: (field: keyof AnnouncementFormData, value: string) => void;
  onSubmit: () => void;
}

export default function PublishAnnouncementForm({ formData, onFormChange, onSubmit }: AnnouncementFormProps) {
  return (
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="5">
        <Stack spacing="4" direction={{ base: 'column', sm: 'row' }} justify="space-between">
          <Box>
            <Text textStyle="lg" fontWeight="medium">
              Publish Announcement
            </Text>
            <Text color="fg.muted" textStyle="sm">
              Provider your announcement details
            </Text>
          </Box>
        </Stack>
        <Divider />
        <Stack spacing="5" divider={<StackDivider />}>
          <FormControl id="title" isRequired>
            <FormLabel>Title</FormLabel>
            <Input
              maxW={{ md: '3xl' }}
              value={formData.title}
              onChange={(e) => onFormChange('title', e.target.value)}
            />
          </FormControl>

          <FormControl id="content" isRequired>
            <FormLabel>Content</FormLabel>
            <Textarea
              maxW={{ md: '3xl' }}
              rows={5}
              resize="none"
              value={formData.content}
              onChange={(e) => onFormChange('content', e.target.value)}
            />
          </FormControl>

          <FormControl id="link">
            <FormLabel>Link (Optional)</FormLabel>
            <Input
              maxW={{ md: '3xl' }}
              value={formData.link}
              onChange={(e) => onFormChange('link', e.target.value)}
            />
          </FormControl>

          <Button alignSelf="flex-end" onClick={onSubmit}>Publish Announcement</Button>
        </Stack>
      </Stack>
    </Container>
  );
};
