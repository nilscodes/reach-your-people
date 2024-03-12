import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Container,
  Divider,
  FormControl,
  FormLabel,
  Heading,
  Input,
  ListItem,
  OrderedList,
  Stack,
  StackDivider,
  Text,
  Textarea,
  UnorderedList,
} from '@chakra-ui/react';
import { AnnouncementFormData } from './PublishAnnouncement';
import Markdown from 'react-markdown';


interface AnnouncementFormProps {
  formData: AnnouncementFormData;
  onFormChange: (field: keyof AnnouncementFormData, value: string) => void;
  onSubmit: () => void;
}

const components = {
  h1: (props: any) => <Heading as="h1" size="lg" {...props} />,
  h2: (props: any) => <Heading as="h2" size="md" {...props} />,
  h3: (props: any) => <Heading as="h3" size="sm" {...props} />,
  p: (props: any) => <Text {...props} py={2} />,
  ol: (props: any) => <OrderedList {...props} />,
  ul: (props: any) => <UnorderedList {...props} />,
  li: (props: any) => <ListItem {...props} />,
};

export default function PublishAnnouncementForm({ formData, onFormChange, onSubmit }: AnnouncementFormProps) {
  const [content, setContent] = useState(formData.content);
  const [title, setTitle] = useState(formData.title);
  const [markdown, setMarkdown] = useState(formData.content);

  useEffect(() => {
    setMarkdown(`# ${title}\n\n${content}`);
  }, [title, content])

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
              maxW={{ md: '50%' }}
              value={formData.title}
              onChange={(e) => {
                onFormChange('title', e.target.value);
                setTitle(e.target.value);
              }}
            />
          </FormControl>

          <Stack direction={{ base: 'column', md: 'row' }} spacing="4">
            <FormControl id="content" isRequired w={{ base: '100%', md: '50%' }}>
              <FormLabel>Content (Markdown allowed)</FormLabel>
              <Textarea
                maxW={{ md: '100%' }}
                rows={15}
                resize="vertical"
                value={formData.content}
                onChange={(e) => {
                  onFormChange('content', e.target.value)
                  setContent(e.target.value)
                }}
              />
            </FormControl>
            <Box w={{ base: '100%', md: '50%' }} pl={{ base: 0, md: 5}}>
              <FormLabel>Preview</FormLabel>
              <Markdown components={components}>{markdown}</Markdown>
            </Box>
          </Stack>

          <FormControl id="link">
            <FormLabel>Link (Optional)</FormLabel>
            <Input
              maxW={{ md: '50%' }}
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
