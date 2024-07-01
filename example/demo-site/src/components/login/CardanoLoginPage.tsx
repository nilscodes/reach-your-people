import { useEffect, useState } from 'react';
import {  Container, Stack, Heading, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, useToast, useDisclosure, Button, Text, HStack } from '@chakra-ui/react'
import { BrowserWallet, Transaction, Wallet, resolveSlotNo } from '@meshsdk/core'
import { signIn } from "next-auth/react";
import { useRouter } from 'next/navigation';
import { Logo } from '@/components/Logo';
import { useApi } from '@/contexts/ApiProvider';
import WalletLogin from '@/components/WalletLogin';
import useTranslation from 'next-translate/useTranslation';
import { MdInfo } from 'react-icons/md';

const expiredSinceMinutes = 1;
const rypPoolId = process.env.NEXT_PUBLIC_RYP_STAKEPOOL ?? 'pool1h6q8jj55dn6727yydlypu0rz4sflf2enxhs0thqydmddgu3shl5'

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
        callbackUrl: '/account',
      });
    } catch (error) {
      // Show chakra ui error toast
      showErrorToast();
    }
  };

  const handleHwSignIn = async (selectedWallet: string) => {
    setHwWallet(selectedWallet);
    onOpen()
  };

  const startHwVerification = async () => {
    onClose()
    try {
      const activeWallet = await BrowserWallet.enable(hwWallet!);
      const rewardAddresses = await activeWallet.getRewardAddresses();
      const stakeAddress = rewardAddresses[0];
      const addresses = await activeWallet.getUsedAddresses();
      
      // Get expired transaction slot and create a transaction that sends ADA to yourself
      let fiveMinutesBefore = new Date(new Date().getTime() - expiredSinceMinutes * 60000);
      const slot = resolveSlotNo('mainnet', fiveMinutesBefore.getTime());
      const tx = new Transaction({ initiator: activeWallet })
        .setTimeToExpire(slot)
        .sendLovelace(
          addresses[0],
          '1000000'
        ).
        delegateStake(stakeAddress, rypPoolId);
      const unsignedTx = await tx.build();
      const signedTx = await activeWallet.signTx(unsignedTx);
      signIn("cardano", {
        stakeAddress,
        txCbor: signedTx,
        callbackUrl: '/account',
      });
    } catch (error) {
      // Show chakra ui error toast
      showErrorToast();
    }
  }

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
    <Modal isOpen={isOpen} onClose={onClose} isCentered size='2xl'>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{t('hardwareWallet.signingTitle')}</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text>
              {t('hardwareWallet.signingDescription')}
            </Text>
            <Text mt="6">
              {t('hardwareWallet.signingDescription2')}
            </Text>
            <HStack alignItems="flex-start" mt="6">
              <MdInfo />
              <Text>
                {t('hardwareWallet.signingDescription3')}
              </Text>
            </HStack>
          </ModalBody>

          <ModalFooter mb="2">
            <Button onClick={startHwVerification}>
              {t('hardwareWallet.signingButton')}
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
  </Container>);
};
