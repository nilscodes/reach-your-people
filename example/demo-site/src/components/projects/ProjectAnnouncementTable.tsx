import {
  Box,
  Container,
  HStack,
  Icon,
  Input,
  InputGroup,
  InputLeftElement,
  Stack,
  Text,
  useBreakpointValue,
} from '@chakra-ui/react'
import { FiSearch } from 'react-icons/fi'
import ProjectAnnouncementTableContent from './ProjectAnnouncementTableContent'
import { Announcement } from '@/lib/ryp-publishing-api';
import { Account } from '@/lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import { useMemo } from 'react';

interface ProjectAnnouncementTableProps {
  projectId: number;
  announcements: Announcement[];
  authors: Record<number, Account>;
}

export const ProjectAnnouncementTable = ({ projectId, announcements, authors }: ProjectAnnouncementTableProps) => {
  const { t } = useTranslation('publish')
  const isMobile = useBreakpointValue({ base: true, md: false })
  const sortedAnnouncements = useMemo(() => announcements.sort((a, b) => new Date(b.createdDate!).getTime() - new Date(a.createdDate!).getTime()), [announcements])
  return (
    <Container py={{ base: '4', md: '8' }} px="0">
      <Box
        bg="bg.surface"
        boxShadow={{ base: 'none', md: 'sm' }}
        borderRadius={{ base: 'none', md: 'lg' }}
      >
        <Stack spacing="5">
          <Box px={{ base: '4', md: '6' }} pt="5">
            <Stack direction={{ base: 'column', md: 'row' }} justify="flex-end">
              {/* <Text textStyle="lg" fontWeight="medium">
                Members
              </Text> */}
              <InputGroup maxW="xs">
                <InputLeftElement pointerEvents="none">
                  <Icon as={FiSearch} color="fg.muted" boxSize="5" />
                </InputLeftElement>
                <Input placeholder={t('announcementsSearchPlaceholder')} />
              </InputGroup>
            </Stack>
          </Box>
          <Box overflowX="auto">
            <ProjectAnnouncementTableContent projectId={projectId} announcements={sortedAnnouncements} authors={authors} />
          </Box>
          <Box px={{ base: '4', md: '6' }} pb="5">
            <HStack spacing="3" justify="space-between">
              {!isMobile && (
                <Text color="fg.muted" textStyle="sm">
                  {t('announcementsPagingFooter', { first: 1, last: announcements.length, total: announcements.length })}
                </Text>
              )}
              {/* <ButtonGroup
                spacing="3"
                justifyContent="space-between"
                width={{ base: 'full', md: 'auto' }}
                variant="secondary"
              >
                <Button>Previous</Button>
                <Button>Next</Button>
              </ButtonGroup> */}
            </HStack>
          </Box>
        </Stack>
      </Box>
    </Container>
  )
}