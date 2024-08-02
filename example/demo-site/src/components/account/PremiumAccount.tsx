import React, { useState } from 'react';
import { 
  Box,
  Stack,
} from '@chakra-ui/react';
import { Account } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';
import PremiumUpgrade from './PremiumUpgrade';
import PremiumInfo from './PremiumInfo';
import { isPremiumAccount } from '@/lib/premium';

type PremiumAccountProps = {
  account: Account;
};

export default function PremiumAccount({ account }: PremiumAccountProps) {
  const { t } = useTranslation('accounts');
  const [isPremium] = useState(isPremiumAccount(account));

  const description = isPremium ? t('premiumAccount.descriptionPremium') : t('premiumAccount.descriptionNonPremium');

  const completeOrder = () => {
    window.location.reload()
  }

  return (<Box
    maxW="3xl"
    mx="auto"
    px="0"
    py={{ base: '6', md: '8', lg: '12' }}
  >
    <Stack spacing="8">
      <Card heading={t('premiumAccount.title', { displayName: account.displayName })} description={description} />
      {isPremium && <PremiumInfo account={account} completeOrder={completeOrder} />}
      {!isPremium && <PremiumUpgrade completeOrder={completeOrder} />}
    </Stack>
  </Box>);
}
