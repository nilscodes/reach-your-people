import React from 'react';
import { Heading, Box, Stack } from '@chakra-ui/react';
import WalletSettingsForm from './WalletSettingsForm';
import { Account, GetLinkedExternalAccounts200ResponseInner } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';

type WalletSettingsListProps = {
  account: Account;
  wallets: GetLinkedExternalAccounts200ResponseInner[];
};

export default function WalletSettingsList({ account, wallets }: WalletSettingsListProps) {
  const { t } = useTranslation('accounts');
  return (<Box
    maxW="3xl"
    mx="auto"
    px="0"
    py={{ base: '6', md: '8', lg: '12' }}
  >
    <Stack spacing="8">
      <Card heading={t('walletsTitle')} description={t('walletsDescription')} />
      <Stack spacing="3">
        {wallets.map((wallet) => {
          return (<WalletSettingsForm
            key={wallet.externalAccount.id}
            wallet={wallet}
          />);
        })}

      </Stack>
    </Stack>
  </Box>);
}