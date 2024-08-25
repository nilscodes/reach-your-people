import React from 'react';
import { Stack } from '@chakra-ui/react';
import { Account } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';
import CardanoSettingsForm from './CardanoSettingsForm';

type GlobalSettingsProps = {
  account: Account;
};

export default function GlobalSettings({ account }: GlobalSettingsProps) {
  const { t } = useTranslation('accounts');
  return (<Stack spacing="8">
    <Card heading={t('globalNotificationsTitle')} description={t('globalNotificationsDescription')} />
    <CardanoSettingsForm account={account} />
  </Stack>);
}