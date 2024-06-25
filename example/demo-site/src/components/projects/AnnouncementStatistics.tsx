import React from 'react';
import { Box, SimpleGrid, Heading, HStack } from '@chakra-ui/react';
import { Account, ListProjects200Response } from '@/lib/ryp-subscription-api';
import { Announcement } from '@/lib/ryp-publishing-api';
import Stat from '../Stat';
import { findProviderByType } from '@/lib/providerutil';
import useTranslation from 'next-translate/useTranslation';
import { Statistics } from '@/lib/types/Statistics';

type AnnouncementStatisticsProps = {
  announcement: Announcement;
  project: ListProjects200Response;
  author: Account;
  views: number;
};

const calculateTotal = (data: Record<string, number> | undefined) => {
  if (data === undefined) return 0;
  return Object.values(data).reduce((acc, curr) => acc + curr, 0);
};

export default function AnnouncementStatistics({ announcement, views }: AnnouncementStatisticsProps) {
  const { sent, uniqueAccounts, explicitSubscribers, delivered, failures } = announcement.statistics as Statistics;
  const { t } = useTranslation('publish');
  const { t: tc } = useTranslation('common');

  const totalViews = views;
  const totalSent = calculateTotal(sent);
  const totalDelivered = calculateTotal(delivered);
  const totalFailures = calculateTotal(failures);

  const allKeys = Array.from(new Set([
    ...Object.keys(sent || {}),
    ...Object.keys(delivered || {}),
    ...Object.keys(failures || {}),
  ]));

  const createStatComponents = (key: string) => {
    const provider = findProviderByType(key);
    return (
      <Box key={key} mt={{ base: 8, md: 16 }}>
        <Heading as="h2" size="sm" mb="4">
          <HStack spacing="4">
            {provider?.Component}
            <span>{tc(`types.${provider?.id}`)}</span>
          </HStack>
        </Heading>
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={{ base: '5', md: '6' }}>
          <Stat
            label={t('statistics.sent')}
            tooltip={t('statistics.sentDescription')}
            value={`${sent?.[key] || 0}`}
          />
          <Stat
            label={t('statistics.delivered')}
            tooltip={t('statistics.deliveredDescription')}
            value={`${delivered?.[key] || 0}`}
          />
          <Stat
            label={t('statistics.failures')}
            tooltip={t('statistics.failuresDescription')}
            value={`${failures?.[key] || 0}`}
          />
        </SimpleGrid>
      </Box>
    );
  };

  return (
    <Box as="section">
      <Box>
        <Heading as="h2" size="sm" mb="4">{t('statistics.announcementTitle')}</Heading>
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={{ base: '5', md: '6' }}>
          <Stat
            label={t('statistics.publishDate')}
            tooltip={t('statistics.publishDateDescription')}
            value={`${new Date(announcement.createdDate!).toLocaleDateString()}`}
          />
        </SimpleGrid>
      </Box>
      <Box mt={{ base: 8, md: 16 }}>
        <Heading as="h2" size="sm" mb="4">{t('statistics.notificationsTitle')}</Heading>
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={{ base: '5', md: '6' }}>
          <Stat
            label={t('statistics.uniqueAccounts')}
            tooltip={t('statistics.uniqueAccountsDescription')}
            value={`${uniqueAccounts ?? 0}`}
          />
          <Stat
            label={t('statistics.excplicitSubscribers')}
            tooltip={t('statistics.excplicitSubscribersDescription')}
            value={`${explicitSubscribers ?? 0}`}
          />
          <Stat
            label={t('statistics.totalViews')}
            tooltip={t('statistics.totalViewsDescription')}
            value={`${totalViews}`}
          />
          <Stat
            label="Total Sent"
            tooltip={t('statistics.sentDescription')}
            value={`${totalSent}`}
          />
          <Stat
            label="Total Delivered"
            tooltip={t('statistics.deliveredDescription')}
            value={`${totalDelivered}`}
          />
          <Stat
            label="Total Failures"
            tooltip={t('statistics.failuresDescription')}
            value={`${totalFailures}`}
          />
        </SimpleGrid>
      </Box>
      
      {allKeys.map(createStatComponents)}
    </Box>
  );
};
