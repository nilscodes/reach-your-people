import {
  Badge,
  Box,
  HStack,
  Icon,
  IconButton,
  Table,
  TableProps,
  Tbody,
  Td,
  Text,
  Th,
  Thead,
  Tr,
} from '@chakra-ui/react'
// import { Rating } from './Rating'
import { Announcement, AnnouncementStatusEnum } from '@/lib/ryp-publishing-api';
import { Account } from '@/lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import { MdQueryStats } from 'react-icons/md';
import { IoMdDocument } from 'react-icons/io';
import NextLink from '../NextLink';
import { IoArrowDown } from 'react-icons/io5';

interface MemberTableProps extends TableProps {
  projectId: number;
  announcements: Announcement[];
  authors: Record<number, Account>;
}

function StatusBadge({ status }: { status: AnnouncementStatusEnum }) {
  const { t } = useTranslation('publish');
  let colorScheme = 'gray';
  switch (status) {
    case 'PENDING':
      colorScheme = 'gray';
      break;
    case 'PREPARED':
      colorScheme = 'blue';
      break;
    case 'PUBLISHED':
      colorScheme = 'green';
      break;
    case 'CANCELLED':
      colorScheme = 'red';
      break;
    case 'PUBLISHING':
      colorScheme = 'yellow';
      break;
  }
  return (<Badge size="sm" colorScheme={colorScheme}>
    {t(`status.${status}`)}
  </Badge>)
}

export default function ProjectAnnouncementTableContent({ announcements, projectId, authors, ...rest }: MemberTableProps) {
  const { t } = useTranslation('publish');
  const getAuthorName = (actorId: string) => {
    const authorId = Number(actorId.split('/').pop())
    return authors[authorId].displayName;
  }

  return (<Table {...rest}>
    <Thead>
      <Tr>
        <Th>
          <HStack spacing="3">
            {/* <Checkbox /> */}
            <HStack spacing="1">
              <Text>{t('publish.form.title')}</Text>
            </HStack>
          </HStack>
        </Th>
        <Th>
          <HStack spacing="1">
            <Text>{t('createdDate')}</Text>
            <Icon as={IoArrowDown} color="fg.muted" boxSize="4" />
          </HStack>
        </Th>
        <Th>{t('publishingStatus')}</Th>
        <Th>{t('publishingAuthor')}</Th>
        {/* <Th>Role</Th> */}
        {/* <Th>Rating</Th> */}
        <Th></Th>
      </Tr>
    </Thead>
    <Tbody>
      {announcements.map((announcement) => (
        <Tr key={announcement.id}>
          <Td>
            <HStack spacing="3">
              {/* <Checkbox /> */}
              {/* <Avatar name={announcement.name} src={announcement.avatarUrl} boxSize="10" /> */}
              <Box>
                <Text fontWeight="medium">{(announcement.announcement as any).object.summary}</Text>
                {/* <Text color="fg.muted">{announcement.handle}</Text> */}
              </Box>
            </HStack>
          </Td>
          <Td>
            <Text color="fg.muted">{new Date(announcement.createdDate!).toLocaleString()}</Text>
          </Td>
          <Td>
            <StatusBadge status={announcement.status!} />
          </Td>
          <Td>
            <Text color="fg.muted">{getAuthorName(announcement.announcement.actor.id)}</Text>
          </Td>
          {/* <Td>
            <Text color="fg.muted">{announcement.announcement.type}</Text>
          </Td> */}
          {/* <Td>
            <Text color="fg.muted">
              <Rating defaultValue={member.rating} size="xl" />
            </Text>
          </Td> */}
          <Td>
            <HStack spacing="1">
              <NextLink href={`/publish/${projectId}/announcements/${announcement.id}/statistics`}>
                <IconButton icon={<MdQueryStats />} variant="tertiary" aria-label={t('viewAnnouncementStatistics')} />
              </NextLink>
              <NextLink href={`/announcements/${announcement.id}`}>
                <IconButton icon={<IoMdDocument />} variant="tertiary" aria-label={t('viewAnnouncement')} />
              </NextLink>
            </HStack>
          </Td>
        </Tr>
      ))}
    </Tbody>
  </Table>)
}
