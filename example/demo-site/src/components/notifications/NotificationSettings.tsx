import React from 'react';
import { Heading, Box, Stack, Text } from '@chakra-ui/react';
import WalletSettingsForm from './WalletSettingsForm';
import { Account, GetLinkedExternalAccounts200ResponseInner } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';
import WalletSettingsList from './WalletSettingsList';
import GlobalSettings from './GlobalSettings';

type NotificationSettingsProps = {
  account: Account;
  wallets: GetLinkedExternalAccounts200ResponseInner[];
};

export default function NotificationSettings({ account, wallets }: NotificationSettingsProps) {
  const { t } = useTranslation('accounts');
  return (<Box
    maxW="3xl"
    mx="auto"
    px="0"
    py={{ base: '6', md: '8', lg: '12' }}
  >
    <GlobalSettings account={account} />
    <WalletSettingsList account={account} wallets={wallets} />
  </Box>);
}