import { Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Button, Text, HStack, useToast } from '@chakra-ui/react'
import { BrowserWallet, Transaction, resolveSlotNo } from '@meshsdk/core'
import useTranslation from 'next-translate/useTranslation';
import { signIn } from "next-auth/react";
import { MdInfo } from 'react-icons/md';

const expiredSinceMinutes = 1;
const rypPoolId = process.env.NEXT_PUBLIC_RYP_STAKEPOOL ?? 'pool1h6q8jj55dn6727yydlypu0rz4sflf2enxhs0thqydmddgu3shl5'

type CardanoHardwareWalletLoginModalProps = {
  isOpen: boolean
  onClose(): void
  currentWallet: string
}

export function CardanoHardwareWalletLoginModal({ isOpen, onClose, currentWallet }: CardanoHardwareWalletLoginModalProps) {
  const { t } = useTranslation('accounts');
  const toast = useToast();

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

  const startHwVerification = async () => {
    try {
      const activeWallet = await BrowserWallet.enable(currentWallet);
      const rewardAddresses = await activeWallet.getRewardAddresses();
      const stakeAddress = rewardAddresses[0];
      const addresses = await activeWallet.getUsedAddresses();
      
      // Get expired transaction slot and create a transaction that sends ADA to yourself to verify payment credentials and a stake delegation certificate to verify the staking credentials.
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
      showErrorToast();
    }
  }

  return (<Modal isOpen={isOpen} onClose={onClose} isCentered size='2xl'>
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
        <Button onClick={() => {
          onClose()
          startHwVerification()
        }}>
          {t('hardwareWallet.signingButton')}
        </Button>
      </ModalFooter>
    </ModalContent>
  </Modal>);
}
