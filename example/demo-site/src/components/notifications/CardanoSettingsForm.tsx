import { Box, Container, Stack, StackDivider, Text } from '@chakra-ui/react'
import { Account, AccountPartialCardanoSettingsEnum } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import { useEffect, useState } from 'react';
import { useApi } from '@/contexts/ApiProvider';
import CardanoSettings from './CardanoSettings';

export default function CardanoSettingsForm({ account }: { account: Account }) {
  const [cardanoSettings, setCardanoSettings] = useState(account.cardanoSettings as AccountPartialCardanoSettingsEnum[]);
  const api = useApi();
  const { t } = useTranslation('accounts');

  useEffect(() => {
    const updateCardanoSettings = async () => {
      await api.updateAccountCardanoSettings(cardanoSettings)
    }
    updateCardanoSettings(); // TODO this calls the update API on every load, because API gets set - does not look right
  }, [cardanoSettings, api])

  return (<Container py={{ base: '4', md: '8' }} px="0">
    <Stack spacing="5" divider={<StackDivider />}>
      <Stack
        direction={{ base: 'column', lg: 'row' }}
        spacing={{ base: '5', lg: '8' }}
        justify="space-between"
      >
        <Box flexShrink={0}>
          <Text textStyle="lg" fontWeight="medium">
            {t('cardanoNotificationSettings')}
          </Text>
        </Box>
        <CardanoSettings maxW={{ base: '5xl', lg: '3xl' }} settings={cardanoSettings} onChangeCardanoSettings={(newCardanoSettings) => setCardanoSettings(newCardanoSettings) } />
      </Stack>
    </Stack>
  </Container>);
}