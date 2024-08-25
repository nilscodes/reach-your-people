import {
  Box,
  BoxProps,
  Stack,
  StackDivider,
  Switch,
  Text,
} from '@chakra-ui/react'
import useTranslation from 'next-translate/useTranslation';
import { AccountPartialCardanoSettingsEnum } from '@/lib/ryp-subscription-api';
import { useState } from 'react';
import { useApi } from '@/contexts/ApiProvider';
import Trans from 'next-translate/Trans';
import NextLink from '../NextLink';

interface CardanoSettingsProps extends BoxProps {
  settings: AccountPartialCardanoSettingsEnum[];
  onChangeCardanoSettings: (newCardanoSettings: AccountPartialCardanoSettingsEnum[]) => void;
}

export default function CardanoSettings({ settings, onChangeCardanoSettings, ...props }: CardanoSettingsProps) {
  const [currentCardanoSettings, setCurrentCardanoSettings] = useState(settings);
  const { t } = useTranslation('accounts');
  const api = useApi();

  const updateCardanoSettings = (on: boolean, settings: AccountPartialCardanoSettingsEnum[]) => {
    let newCardanoSettings = [ ...currentCardanoSettings ];
    settings.forEach((setting) => {
      newCardanoSettings = newCardanoSettings.filter((s) => s !== setting);
    });
    if (on) {
      newCardanoSettings.push(...settings);
    }
    setCurrentCardanoSettings(newCardanoSettings);
    onChangeCardanoSettings(newCardanoSettings);
  }

  const governanceEnabled = currentCardanoSettings.includes(AccountPartialCardanoSettingsEnum.GovernanceActionAnnouncements);

  return (<Box as="form" bg="bg.surface" boxShadow="sm" borderRadius="lg" {...props}>
    <Stack spacing="5" px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }} divider={<StackDivider />}>
      <Stack justify="space-between" direction="row" spacing="16">
        <Stack spacing="0.5" fontSize="sm">
          <Text color="fg.emphasized" fontWeight="bold">
            {t('cardanoSettings.governance.title')}
          </Text>
          <Text color="fg.muted">
            <Trans i18nKey='accounts:cardanoSettings.governance.description' components={[<NextLink key="" href='/projects/drep' />]}></Trans>
          </Text>
        </Stack>
        <Switch colorScheme="brand" isChecked={governanceEnabled} onChange={(e) => updateCardanoSettings(e.target.checked, [AccountPartialCardanoSettingsEnum.GovernanceActionAnnouncements])} />
      </Stack>
    </Stack>
  </Box>);
}
