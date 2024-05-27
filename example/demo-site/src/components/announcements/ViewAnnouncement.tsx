import {
  AspectRatio,
  Avatar,
  Box,
  Container,
  Heading,
  HStack,
  Image,
  Link,
  Skeleton,
  Stack,
  Text,
} from '@chakra-ui/react'
import { useApi } from '@/contexts/ApiProvider';
import useTranslation from 'next-translate/useTranslation';
import Markdown from 'react-markdown';
import { Account, ListProjects200Response } from '@/lib/ryp-subscription-api';
import { Announcement } from '@/lib/ryp-publishing-api';
import { components } from '../chakraMarkdownComponents';
import NextLink from '../NextLink';

type ViewAnnouncementProps = {
  announcement: Announcement;
  project: ListProjects200Response;
  author: Account;
};

function formatDate(isoDateString: string) {
  const date = new Date(isoDateString);

  const formatter = new Intl.DateTimeFormat('en-US', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    second: 'numeric',
  });

  return formatter.format(date);
}

export default function ViewAnnouncement({ announcement, project, author }: ViewAnnouncementProps) {
  const api = useApi();
  const { t } = useTranslation('projects');

  const content = (announcement.announcement as any).object.content;
  const summary = (announcement.announcement as any).object.summary;
  const url = (announcement.announcement as any).object.url;
  const date = (announcement.announcement as any).published;

  return (
    <Container py={{ base: '16', md: '24' }}>
      <Box maxW="3xl" mx="auto">
        <Stack spacing="6">
          {/* <AspectRatio ratio={16 / 9}> */}
            <NextLink href={`/projects/${project.id}`} display='flex'>
              <Image
                flexGrow={1}
                src={project.logo}
                objectPosition="center"
                objectFit="contain"
                height="10rem"
                alt={summary ?? ''}
                fallback={<Skeleton />}
                borderRadius="xl"
              />
            </NextLink>
          {/* </AspectRatio> */}
          <Stack spacing="3">
            <Stack spacing="1">
              <Heading
                size="xs"
                fontWeight="semibold"
                fontSize={{ base: '2xl', lg: '5xl' }}
                lineHeight={{ base: '1.5', lg: '2rem' }}
              >
                {summary}
              </Heading>
            </Stack>
            <Markdown components={components}>{content}</Markdown>
          </Stack>
          {url && (<HStack>
            <Text fontSize="sm" color="default">{t('sourceUrl')}</Text>
            <Text fontSize="sm">
              <Link href={url} isExternal>{url}</Link>
            </Text>
          </HStack>)}
          <HStack spacing="3">
            <Avatar size="md" name={author.displayName} />
            <Box lineHeight="1.25rem">
              <Text fontSize="sm" color="default">
                {author.displayName}
              </Text>
              <Text fontSize="sm"> {formatDate(date)}</Text>
            </Box>
          </HStack>
        </Stack>
      </Box>
    </Container>);
}