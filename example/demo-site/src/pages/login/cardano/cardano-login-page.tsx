import { useEffect, useState } from 'react';
import {  Container, Stack, Heading, Img, useToast} from '@chakra-ui/react'
import { BrowserWallet, Wallet } from '@meshsdk/core'
import { signIn } from "next-auth/react";
import { useRouter } from 'next/navigation';
import { Logo } from '@/components/Logo';
import { useApi } from '@/contexts/ApiProvider';
import WalletLogin from '@/components/WalletLogin';

export default function CardanoLoginPage() {
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const router = useRouter()
  const api = useApi();
  const toast = useToast();

  useEffect(() => {
    setWallets(BrowserWallet.getInstalledWallets());
  }, []);

  const handleSignIn = async (selectedWallet: string) => {
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
      // Show chakra ui error toast
      toast({
        title: 'Could not get signature from wallet. The signing flow was cancelled.',
        status: "error",
        duration: 15000,
        isClosable: true,
        position: "top",
        variant: "solid",
      });
    }
  };

  return (<Container maxW="md" py={{ base: '12', md: '24' }}>
    <Stack spacing="8">
      <Stack spacing="6">
        <Logo />
        <Stack spacing={{ base: '2', md: '3' }} textAlign="center">
          <Heading size={{ base: 'xs', md: 'sm' }}>Log in to your account</Heading>
        </Stack>
      </Stack>
      <WalletLogin wallets={wallets} handleSignIn={handleSignIn} onReturn={() => {
        router.push('/login');
      }} />
    </Stack>
  </Container>);
};