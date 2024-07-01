import React, { useEffect, useState } from 'react';
import { Container, Button, Stack, useToast, StackDivider, useDisclosure } from '@chakra-ui/react';
import { Account, GetLinkedExternalAccounts200ResponseInner } from '../lib/ryp-subscription-api';
import { CardanoIcon, providerList, providersConfig } from './ProviderIcons';
import { signIn } from 'next-auth/react';
import { LinkedAccount } from './LinkedAccount';
import WalletLogin from './WalletLogin';
import { BrowserWallet, Wallet } from '@meshsdk/core';
import { useApi } from '@/contexts/ApiProvider';
import { MdPhone, MdWeb } from 'react-icons/md';
import PhoneVerification from './phone/PhoneVerification';
import useTranslation from 'next-translate/useTranslation';
import PushApiVerification from './pushapi/PushApiVerification';
import { VibrantSyncStatus } from '@/lib/types/VibrantSyncStatus';
import { VibrantSyncStatusMessage } from './linkedaccounts/VibrantSyncStatusMessage';
import ReferralLink from './account/ReferralLink';
import useReferral from './hooks/useReferral';
import Card from './Card';
import FirstSteps from './account/FirstSteps';
import { FirstStepsItems, bitmaskToEnum } from '@/lib/types/FirstSteps';
import { CardanoHardwareWalletLoginModal } from './login/CardanoHardwareLogin';
import cardanoWalletLogin from './login/CardanoLogin';

type LinkedAccountsProps = {
  account: Account;
  linkedAccounts: GetLinkedExternalAccounts200ResponseInner[];
  accountSettings: Record<string, string>;
};

const isWalletExternalAccount = (type: string) => type === 'cardano';

export function sortLinkedExternalAccounts(linkedAccounts: GetLinkedExternalAccounts200ResponseInner[]) {
  return [...linkedAccounts].sort((a, b) => a.externalAccount.id! - b.externalAccount.id!);
}

export default function LinkedAccounts({ account, accountSettings, linkedAccounts: linkedAccountsProp }: LinkedAccountsProps) {
  const api = useApi();
  const toast = useToast();
  const { t } = useTranslation('accounts');
  const [referral, completeReferral] = useReferral();
  const [linkedAccounts, setLinkedAccounts] = React.useState<GetLinkedExternalAccounts200ResponseInner[]>(sortLinkedExternalAccounts(linkedAccountsProp));
  const [showWalletConnection, setShowWalletConnection] = React.useState(false);
  const [showPhoneConnection, setShowPhoneConnection] = React.useState(false);
  const [showPushApiConnection, setShowPushApiConnection] = React.useState(false);
  const [firstSteps, setFirstSteps] = useState(bitmaskToEnum(+accountSettings['FIRST_STEPS'] ?? 0));
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const [hwWallet, setHwWallet] = useState<string | null>(null);
  const { isOpen, onOpen, onClose } = useDisclosure();

  const handleSignIn = (provider: string) => {
    signIn(provider);
  };

  const handleWalletSignIn = async (selectedWallet: string) => {
    try {
      await cardanoWalletLogin(selectedWallet, api);
    } catch (error) {
      toast({
        title: t('walletSignInCancelled'),
        status: "error",
        duration: 15000,
        isClosable: true,
        position: "top",
        variant: "solid",
      });
    }
  };

  const handleHwSignIn = async (selectedWallet: string) => {
    setHwWallet(selectedWallet);
    onOpen()
  };

  useEffect(() => {
    const submitReferral = async (referredBy: number) => {
      await api.submitReferredBy(referredBy);
    }
    if (referral) {
      submitReferral(referral);
      completeReferral();
    }
  }, [api, referral, completeReferral]);

  useEffect(() => {
    if (showWalletConnection) {
      setWallets(BrowserWallet.getInstalledWallets());
    }
  }, [showWalletConnection]);

  const unlinkExternalAccount = async (externalAccountId: number) => {
    await api.unlinkExternalAccount(externalAccountId);
    setLinkedAccounts(sortLinkedExternalAccounts(linkedAccounts.filter((linkedAccount) => linkedAccount.externalAccount.id !== externalAccountId)));
  };

  const makeDefaultForNotifications = async (externalAccountId: number) => {
    setLinkedAccounts(sortLinkedExternalAccounts(await api.makeDefaultForNotifications(externalAccountId)));
  }

  const finalizePhoneAuth = async () => {
    setShowPhoneConnection(false);
    setLinkedAccounts(sortLinkedExternalAccounts(await api.getLinkedExternalAccounts()));
    const firstStepsSetting = await api.updateFirstSteps();
    setFirstSteps(bitmaskToEnum(+firstStepsSetting.value));
  };

  const finalizePushApi = async () => {
    setShowPushApiConnection(false);
    setLinkedAccounts(sortLinkedExternalAccounts(await api.getLinkedExternalAccounts()));
    const firstStepsSetting = await api.updateFirstSteps();
    setFirstSteps(bitmaskToEnum(+firstStepsSetting.value));
  }

  const onFinishSteps = async (firstSteps: FirstStepsItems[]) => {
    setFirstSteps(firstSteps);
  }

  const isNonSocialAccount = (type: string) => ['sms', 'pushapi'].includes(type);

  const canRemove = linkedAccounts.filter((linkedAccount) => providerList.some((provider) => provider.id === linkedAccount.externalAccount.type)).length > 1;
  const unlinkedProviders = providersConfig.filter((provider) => !linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === provider.id));
  const hasSms = linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === 'sms');
  const hasPushApi = linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === 'pushapi');
  const showSocialConnections = !showWalletConnection && !showPhoneConnection && !showPushApiConnection;
  const showFirstSteps = !firstSteps.includes(FirstStepsItems.Completed) && !firstSteps.includes(FirstStepsItems.Cancelled);

  return (<Container maxW="3xl" py={{ base: '0', md: '12' }} px="0">
    <Stack spacing="8" gap="0">
      {showFirstSteps === true && (<FirstSteps account={account} accountSettings={accountSettings} linkedAccounts={linkedAccounts} onFinishSteps={onFinishSteps} />)}
      <ReferralLink accountSettings={accountSettings} />
      {accountSettings['VIBRANT_SYNC_STATUS'] === VibrantSyncStatus.CompletedConfirmed && (<VibrantSyncStatusMessage />)}
      <Card heading={t('linkedAccounts')} description={t('linkedAccountsDescription')}>
        <Stack spacing="5" divider={<StackDivider />} pt="5">
          {linkedAccounts.map((linkedAccount) => {
            return (<LinkedAccount
              key={linkedAccount.externalAccount.id}
              linkedAccount={linkedAccount}
              icon={providerList.find((provider) => provider.id === linkedAccount.externalAccount.type)?.Component || <CardanoIcon />}
              showUrl
              canRemove={canRemove || (isWalletExternalAccount(linkedAccount.externalAccount.type) && linkedAccounts.length > 1) || isNonSocialAccount(linkedAccount.externalAccount.type)}
              onRemove={unlinkExternalAccount}
              makeDefaultForNotifications={makeDefaultForNotifications}
            />);
          })}
        </Stack>
      </Card>
      <Card heading={t('connectAccounts')} description={t('connectAccountsDescription')}>
        {showWalletConnection && (<WalletLogin wallets={wallets} handleSignIn={handleWalletSignIn} handleHwSignIn={handleHwSignIn} onReturn={() => setShowWalletConnection(false)} />)}
        {showPhoneConnection && (<PhoneVerification onReturn={finalizePhoneAuth} />)}
        {showPushApiConnection && (<PushApiVerification onReturn={finalizePushApi} />)}
        {showSocialConnections && (<Stack spacing="3">
          <Button key="cardano"
            variant="secondary"
            leftIcon={<CardanoIcon />}
            cursor="pointer"
            onClick={() => setShowWalletConnection(true)}
          >
            {t('cardanoWallet')}
          </Button>
          {!hasSms && <Button key="phone"
            variant="secondary"
            leftIcon={<MdPhone />}
            cursor="pointer"
            onClick={() => setShowPhoneConnection(true)}
          >
            {t('mobilePhoneSms')}
          </Button>}
          {!hasPushApi && <Button key="pushapi"
            variant="secondary"
            leftIcon={<MdWeb />}
            cursor="pointer"
            onClick={() => setShowPushApiConnection(true)}
          >
            {t('pushApi')}
          </Button>}
          {unlinkedProviders.map((provider) => {
              return (
                <Button key={provider.id}
                  variant="secondary"
                  leftIcon={provider.Component && provider.Component}
                  cursor="pointer"
                  onClick={() => {
                    handleSignIn(provider.id);
                  }}
                >
                  {provider.name}
                </Button>
              );
            })}
        </Stack>)}
      </Card>
    </Stack>
    <CardanoHardwareWalletLoginModal isOpen={isOpen} onClose={onClose} currentWallet={hwWallet!} />
  </Container>);
};
