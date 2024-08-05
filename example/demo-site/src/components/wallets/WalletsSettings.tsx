import {
  Box,
  BoxProps,
  Skeleton,
  Stack,
  StackDivider,
  Switch,
  Tag,
  Text,
  VStack,
  Wrap,
} from '@chakra-ui/react'
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';
import Trans from 'next-translate/Trans';
import { LinkExternalAccount200Response, LinkExternalAccount200ResponseSettingsEnum } from '@/lib/ryp-subscription-api';
import { useEffect, useState } from 'react';
import { useApi } from '@/contexts/ApiProvider';
import { StakepoolDetails } from '@/lib/types/StakepoolDetails';

interface WalletSettingsProps extends BoxProps {
  wallet: LinkExternalAccount200Response;
  onChangeWalletSettings: (newWalletSettings: LinkExternalAccount200Response) => void;
}

export default function WalletSettings({ wallet, onChangeWalletSettings, ...props }: WalletSettingsProps) {
  const [currentWalletSettings, setCurrentWalletSettings] = useState(wallet);
  const [stakepoolLoading, setStakepoolLoading] = useState(true);
  const [stakepoolDetails, setStakepoolDetails] = useState<StakepoolDetails | null>(null);
  const { t } = useTranslation('accounts');
  const api = useApi();

  const updateWalletSettings = (on: boolean, settings: LinkExternalAccount200ResponseSettingsEnum[]) => {
    const newWalletSettings = { ...currentWalletSettings };
    settings.forEach((setting) => {
      newWalletSettings.settings = newWalletSettings.settings?.filter((s) => s !== setting);
    });
    if (on) {
      newWalletSettings.settings?.push(...settings);
    }
    setCurrentWalletSettings(newWalletSettings);
    onChangeWalletSettings(newWalletSettings);
  }

  useEffect(() => {
    const loadStakepool = async () => {
      setStakepoolLoading(true);
      api.getStakepoolDetailsForStakeAddress(wallet.externalAccount.referenceId)
        .then((poolForWallet) => {
          setStakepoolDetails(poolForWallet);
        })
        .finally(() => {
          setStakepoolLoading(false);
        });
    }
    loadStakepool();
  }, []);

  const tokensEnabled = currentWalletSettings.settings?.includes(LinkExternalAccount200ResponseSettingsEnum.FungibleTokenAnnouncements)
    || currentWalletSettings.settings?.includes(LinkExternalAccount200ResponseSettingsEnum.NonFungibleTokenAnnouncements)
    || currentWalletSettings.settings?.includes(LinkExternalAccount200ResponseSettingsEnum.RichFungibleTokenAnnouncements);
  const spoEnabled = currentWalletSettings.settings?.includes(LinkExternalAccount200ResponseSettingsEnum.StakepoolAnnouncements);
  // const drepEnabled = currentWalletSettings.settings?.includes(LinkExternalAccount200ResponseSettingsEnum.DrepAnnouncements);

  return (<Box as="form" bg="bg.surface" boxShadow="sm" borderRadius="lg" {...props}>
    <Stack spacing="5" px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }} divider={<StackDivider />}>
      <Stack justify="space-between" direction="row" spacing="16">
        <Stack spacing="0.5" fontSize="sm">
          <Text color="fg.emphasized" fontWeight="bold">
            {t('settings.tokens.title')}
          </Text>
          <Text color="fg.muted">
            <Trans i18nKey='accounts:settings.tokens.description' components={[<NextLink key="" href='/projects' />]}></Trans>
          </Text>
        </Stack>
        <Switch colorScheme="brand" isChecked={tokensEnabled} onChange={(e) => updateWalletSettings(e.target.checked, [LinkExternalAccount200ResponseSettingsEnum.FungibleTokenAnnouncements, LinkExternalAccount200ResponseSettingsEnum.NonFungibleTokenAnnouncements, LinkExternalAccount200ResponseSettingsEnum.RichFungibleTokenAnnouncements])} />
      </Stack>
      <Stack justify="space-between" direction="row" spacing="16">
        <Stack spacing="0.5" fontSize="sm">
          <Text color="fg.emphasized" fontWeight="bold">
            {t('settings.spo.title')}
          </Text>
          <Text color="fg.muted">
            <Trans i18nKey='accounts:settings.spo.description' components={[<NextLink key="" href='/projects/spo' />]}></Trans>
          </Text>
        </Stack>
        <Stack direction={{ base: 'column-reverse', md: 'row' }} alignItems={{ base: 'flex-end', md: 'flex-start' }} justifyContent='flex-end' spacing="4">
          <Box>
            {stakepoolLoading && (<Skeleton height="6" width="15" />)}
            {!stakepoolLoading && stakepoolDetails !== null && (<Wrap spacing="2"><Tag title={stakepoolDetails.name}>{stakepoolDetails.ticker}</Tag></Wrap>)}
          </Box>
          <Switch colorScheme="brand" isChecked={spoEnabled} onChange={(e) => updateWalletSettings(e.target.checked, [LinkExternalAccount200ResponseSettingsEnum.StakepoolAnnouncements])} />
        </Stack>
      </Stack>
      <Stack justify="space-between" direction="row" spacing="16">
        <Stack spacing="0.5" fontSize="sm">
          <Text color="fg.emphasized" fontWeight="bold">
            {t('settings.drep.title')}
          </Text>
          <Text color="fg.muted">
            <Trans i18nKey='accounts:settings.drep.description' components={[<NextLink key="" href='/projects/drep' />]}></Trans>
          </Text>
        </Stack>
        <VStack alignItems="flex-end">
          <Switch colorScheme="brand" isChecked={false} disabled onChange={(e) => updateWalletSettings(e.target.checked, [LinkExternalAccount200ResponseSettingsEnum.DrepAnnouncements])} />
          <Tag colorScheme="brand">{t('soonTag')}</Tag>
        </VStack>
      </Stack>
    </Stack>
  </Box>);
}
