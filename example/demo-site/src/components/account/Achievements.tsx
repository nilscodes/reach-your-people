import React, { useEffect } from 'react';
import { Box, Stack } from '@chakra-ui/react';
import { Account } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';
import { Achievement } from '@/lib/types/Achievement';
import { useApi } from '@/contexts/ApiProvider';
import AchievementBlock from './AchievementBlock';

type AchievementsProps = {
  account: Account;
  achievements: Achievement[];
};

export default function Achievements({ account, achievements: achievementsProp }: AchievementsProps) {
  const { t } = useTranslation('accounts');
  const api = useApi();
  const [achievements, setAchievements] = React.useState(achievementsProp);

  useEffect(() => {
    async function loadAchievements() {
      setAchievements(await api.getAchievements());
    }
    if (achievements.length === 0) {
      loadAchievements();
    }
  }, [api, achievements.length]);


  return (<Box
    maxW="3xl"
    mx="auto"
    px="0"
    py={{ base: '6', md: '8', lg: '12' }}
  >
    <Stack spacing="8">
      <Card heading={t('achievements.title', { displayName: account.displayName })} description={t('achievements.description')} />
      {achievements.map((achievement) => (
        <AchievementBlock key={achievement.id} achievement={achievement} />
      ))}
    </Stack>
  </Box>);
}