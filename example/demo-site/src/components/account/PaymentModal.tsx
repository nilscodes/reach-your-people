import React, { useEffect, useState } from 'react';
import { 
  Button,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  Text,
  FormControl,
  FormLabel,
  Select,
  useToast,
  Img,
  Stack,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import { useApi } from '@/contexts/ApiProvider';
import { BrowserWallet, resolveSlotNo, Transaction, Wallet } from '@meshsdk/core';

const premiumRecipientAddress = process.env.NEXT_PUBLIC_RYP_PAYMENT_ADDRESS!

type PaymentModalProps = {
  isOpen: boolean;
  header: string
  completeOrder: (transactionId: string) => void;
  paymentAborted: () => void;
};

export default function PaymentModal({ isOpen, completeOrder, paymentAborted, header }: PaymentModalProps) {
  const { t } = useTranslation('accounts');
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const [selectWallet, setSelectWallet] = useState(false);
  const [duration, setDuration] = useState('month');
  const toast = useToast();
  const api = useApi();

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

  useEffect(() => {
    setWallets(BrowserWallet.getInstalledWallets());
  }, []);

  const buyPremium = async (walletName: string) => {
    try {
      const paymentAmount = duration === 'month' ? 15000000 : 150000000;
      const activeWallet = await BrowserWallet.enable(walletName);
      
      // Get transaction slot that is valid for 15 minutes to the premium payment address
      const inFifteenMinutes = new Date(new Date().getTime() + 15 * 60000);
      const slot = resolveSlotNo('mainnet', inFifteenMinutes.getTime());
      const tx = new Transaction({ initiator: activeWallet })
        .setTimeToExpire(slot)
        .sendLovelace(
          premiumRecipientAddress,
          `${paymentAmount}`
        );
      const unsignedTx = await tx.build();
      const transactionId = await activeWallet.signTx(unsignedTx);
      await api.submitOrder(transactionId, duration);
      window.sessionStorage.setItem('premiumBuyInProgress', transactionId);
      completeOrder(transactionId);
    } catch (error) {
      console.log(error);
      showErrorToast();
    }
  }

  return (<Modal isOpen={isOpen} onClose={paymentAborted} isCentered size={{ base: 'sm', lg: 'xl' }}>
    <ModalOverlay />
    <ModalContent>
      <ModalHeader>{header}</ModalHeader>
      <ModalCloseButton />
      <ModalBody>
        {!selectWallet && (<>
          <Text mb="6">{t('premiumAccount.buy.description')}</Text>
          <FormControl id="productSelection" isRequired>
            <FormLabel>{t('premiumAccount.buy.productSelection')}</FormLabel>
            <Select onChange={(e) => setDuration(e.currentTarget.value)}>
              <option key="month" value="month">{t('premiumAccount.buy.1month')}</option>
              <option key="year" value="year">{t('premiumAccount.buy.1year')}</option>
            </Select>
          </FormControl>
        </>)}
        {selectWallet && (<>
          <Text mb="6">{t('premiumAccount.buy.walletSelection')}</Text>
          <Stack spacing="3">
            {wallets.map((wallet) => (
              <Button key={wallet.name}
                variant="secondary"
                cursor="pointer"
                leftIcon={<Img src={wallet.icon} alt={wallet.name} h='1.5em' w='1.5em' />}
                onClick={() => {
                  buyPremium(wallet.name);
                }}
              >
                {wallet.name}
              </Button>)
            )}
          </Stack>
        </>)}
      </ModalBody>
      <ModalFooter>
        <Button onClick={() => {
          setSelectWallet(false);
          paymentAborted();
        }} variant="outline">
          {t('premiumAccount.buy.cancelButton')}
        </Button>
        {!selectWallet && (<Button onClick={() => setSelectWallet(true)} ml={4}>
          {t('premiumAccount.buy.next')}
        </Button>)}
      </ModalFooter>
    </ModalContent>
  </Modal>)
}