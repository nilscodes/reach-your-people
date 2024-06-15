import { Box, Container, Stack, StackDivider, Text } from '@chakra-ui/react'
import { LinkExternalAccount200Response } from '../../lib/ryp-subscription-api';
import WalletSettings from './WalletsSettings';
import useTranslation from 'next-translate/useTranslation';
import { useEffect, useState } from 'react';
import { useApi } from '@/contexts/ApiProvider';

export default function WalletSettingsForm({ wallet }: { wallet: LinkExternalAccount200Response }) {
  const [currentWalletSettings, setCurrentWalletSettings] = useState(wallet);
  const api = useApi();
  const { t } = useTranslation('accounts');

  useEffect(() => {
    const updateWalletSettings = async () => {
      await api.updateLinkedExternalAccountSettings(wallet.externalAccount.id!, currentWalletSettings.settings!)
    }
    updateWalletSettings();    
  }, [currentWalletSettings, api, wallet.externalAccount.id])

  return (<Container py={{ base: '4', md: '8' }} px="0">
    <Stack spacing="5" divider={<StackDivider />}>
      <Stack
        direction={{ base: 'column', lg: 'row' }}
        spacing={{ base: '5', lg: '8' }}
        justify="space-between"
      >
        <Box flexShrink={0}>
          <Text textStyle="lg" fontWeight="medium">
            {wallet.externalAccount.displayName}
          </Text>
          <Text color="fg.muted" textStyle="sm">
            {t('walletSettings')}
          </Text>
        </Box>
        <WalletSettings maxW={{ base: '5xl', lg: '3xl' }} wallet={currentWalletSettings} onChangeWalletSettings={(newWalletSettings) => setCurrentWalletSettings(newWalletSettings) } />
      </Stack>
    </Stack>
  </Container>);
}