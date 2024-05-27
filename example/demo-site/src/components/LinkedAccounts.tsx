import React, { useEffect, useState } from 'react';
import { Container, Heading, Text, Button, Box, VStack, Stack, useToast } from '@chakra-ui/react';
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

type LinkedAccountsProps = {
  account: Account;
  linkedAccounts: GetLinkedExternalAccounts200ResponseInner[];
  accountSettings: Record<string, string>;
};

const isWalletExternalAccount = (type: string) => type === 'cardano';

export default function LinkedAccounts({ accountSettings, linkedAccounts: linkedAccountsProp }: LinkedAccountsProps) {
  const api = useApi();
  const toast = useToast();
  const { t } = useTranslation('accounts');
  const [linkedAccounts, setLinkedAccounts] = React.useState<GetLinkedExternalAccounts200ResponseInner[]>(linkedAccountsProp);
  const [showWalletConnection, setShowWalletConnection] = React.useState(false);
  const [showPhoneConnection, setShowPhoneConnection] = React.useState(false);
  const [showPushApiConnection, setShowPushApiConnection] = React.useState(false);
  const [wallets, setWallets] = useState<Wallet[]>([]);

  const handleSignIn = (provider: string) => {
    signIn(provider);
  };

  const handleWalletSignIn = async (selectedWallet: string) => {
    const activeWallet = await BrowserWallet.enable(selectedWallet);
    const rewardAddresses = await activeWallet.getRewardAddresses();
    const stakeAddress = rewardAddresses[0];
    const addresses = await activeWallet.getUsedAddresses();
    const nonceResponse = await api.createNonce(addresses[0], stakeAddress);
    try {
      const signature = await activeWallet.signData(stakeAddress, nonceResponse.nonce);
      signIn("cardano", {
        stakeAddress,
        signature: JSON.stringify(signature),
        callbackUrl: '/dashboard',
      });
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

  useEffect(() => {
    if (showWalletConnection) {
      setWallets(BrowserWallet.getInstalledWallets());
    }
  }, [showWalletConnection]);

  const unlinkExternalAccount = async (externalAccountId: number) => {
    await api.unlinkExternalAccount(externalAccountId);
    setLinkedAccounts(linkedAccounts.filter((linkedAccount) => linkedAccount.externalAccount.id !== externalAccountId));
  };

  const finalizePhoneAuth = async () => {
    setShowPhoneConnection(false);
    setLinkedAccounts(await api.getLinkedExternalAccounts());
  };

  const finalizePushApi = async () => {
    setShowPushApiConnection(false);
    setLinkedAccounts(await api.getLinkedExternalAccounts());
  }

  const isNonSocialAccount = (type: string) => ['sms', 'pushapi'].includes(type);

  const canRemove = linkedAccounts.filter((linkedAccount) => providerList.some((provider) => provider.id === linkedAccount.externalAccount.type)).length > 1;
  const unlinkedProviders = providersConfig.filter((provider) => !linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === provider.id));
  const hasSms = linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === 'sms');
  const hasPushApi = linkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === 'pushapi');
  const showSocialConnections = !showWalletConnection && !showPhoneConnection && !showPushApiConnection;

  return (<Container maxW="md" py={{ base: '12', md: '24' }}>
    <Stack spacing="8">
      <Heading size={{ base: 'xs', md: 'sm' }}>{t('linkedAccounts')}</Heading>
      {accountSettings['VIBRANT_SYNC_STATUS'] === VibrantSyncStatus.CompletedConfirmed && (<VibrantSyncStatusMessage />)}
      <Stack spacing="3">
        {linkedAccounts.map((linkedAccount) => {
            return (<LinkedAccount
              key={linkedAccount.externalAccount.id}
              linkedAccount={linkedAccount}
              icon={providerList.find((provider) => provider.id === linkedAccount.externalAccount.type)?.Component || <CardanoIcon />}
              showUrl
              canRemove={canRemove || (isWalletExternalAccount(linkedAccount.externalAccount.type) && linkedAccounts.length > 1) || isNonSocialAccount(linkedAccount.externalAccount.type)}
              onRemove={unlinkExternalAccount}
            />);
          })}
      </Stack>
      <Heading size={{ base: 'xs', md: 'sm' }}>{t('connectAccounts')}</Heading>
        {showWalletConnection && (<WalletLogin wallets={wallets} handleSignIn={handleWalletSignIn} onReturn={() => setShowWalletConnection(false)} />)}
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
    </Stack>
  </Container>);
};
