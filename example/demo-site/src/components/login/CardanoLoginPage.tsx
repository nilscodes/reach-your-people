import { useEffect, useState } from 'react';
import {  Container, Stack, Heading, useToast, useDisclosure } from '@chakra-ui/react'
import { BrowserWallet, Wallet } from '@meshsdk/core'
import { signIn } from "next-auth/react";
import { useRouter } from 'next/navigation';
import { Logo } from '@/components/Logo';
import { useApi } from '@/contexts/ApiProvider';
import WalletLogin from '@/components/WalletLogin';
import useTranslation from 'next-translate/useTranslation';
import { CardanoHardwareWalletLoginModal } from './CardanoHardwareLogin';
import { RypSiteApi } from '@/lib/api';
import cardanoWalletLogin from './CardanoLogin';

export default function CardanoLoginPage() {
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const [hwWallet, setHwWallet] = useState<string | null>(null);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const router = useRouter()
  const api = useApi();
  const toast = useToast();
  const { t } = useTranslation('accounts');

  useEffect(() => {
    setWallets(BrowserWallet.getInstalledWallets());
  }, []);

  function showErrorToast() {
    toast({
      title: t('walletSignInCancelled'),
      status: "error",
      duration: 15000,
      isClosable: true,
      position: "top",
      variant: "solid",
    });
  }

  const handleSignIn = async (selectedWallet: string) => {
    try {
      await cardanoWalletLogin(selectedWallet, api);
    } catch (error) {
      // Show chakra ui error toast
      showErrorToast();
    }
  };

  const handleHwSignIn = async (selectedWallet: string) => {
    setHwWallet(selectedWallet);
    onOpen()
  };

  return (<Container maxW="md" py={{ base: '12', md: '24' }}>
    <Stack spacing="8">
      <Stack spacing="6">
        <Logo />
        <Stack spacing={{ base: '2', md: '3' }} textAlign="center">
          <Heading size={{ base: 'xs', md: 'sm' }}>{t('loginTitle')}</Heading>
        </Stack>
      </Stack>
      <WalletLogin wallets={wallets} handleSignIn={handleSignIn} handleHwSignIn={handleHwSignIn} onReturn={() => {
        router.push('/login');
      }} />
    </Stack>
    <CardanoHardwareWalletLoginModal isOpen={isOpen} onClose={onClose} currentWallet={hwWallet!} />
  </Container>);
};
