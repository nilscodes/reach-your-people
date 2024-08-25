import React from 'react';
import { Heading, Box, Stack, Text } from '@chakra-ui/react';
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
  return (<Stack spacing="8">
    <Card heading={t('walletsTitle')} description={t('walletsDescription')} />
    {wallets.length === 0 && (<>
      <Heading size="md">{t('noWallets')}</Heading>
      <Text color="fg.muted">{t('noWalletsDescription')}</Text>
    </>)}
    {wallets.length && (<Stack spacing="3">
      {wallets.map((wallet) => {
        return (<WalletSettingsForm
          key={wallet.externalAccount.id}
          wallet={wallet}
        />);
      })}
    </Stack>)}
  </Stack>);
}