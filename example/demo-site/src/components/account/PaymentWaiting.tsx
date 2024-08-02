import React, { useEffect, useState } from 'react';
import {
  Box,
  Spinner,
  Text,
  IconButton,
  useClipboard,
  Tooltip,
  Flex,
  useToast
} from '@chakra-ui/react';
import { MdContentCopy } from 'react-icons/md';
import useTranslation from 'next-translate/useTranslation';
import { useApi } from '@/contexts/ApiProvider';

type PaymentWaitingProps = {
  transactionId: string;
  completeOrder: () => void;
};

export default function PaymentWaiting({ transactionId, completeOrder }: PaymentWaitingProps) {
  const { t } = useTranslation('accounts');
  const { onCopy } = useClipboard(transactionId);
  const toast = useToast();
  const api = useApi();

  useEffect(() => {
    const checkInterval = setInterval(async () => {
        try {
          const bill = await api.getOrderStatus('cardano', transactionId);
          if (bill.amountReceived && bill.amountReceived > bill.amountRequested) {
            window.sessionStorage.removeItem('premiumBuyInProgress');
            clearInterval(checkInterval!);
            completeOrder()
          } else if (bill.amountReceived !== null && bill.amountReceived !== undefined && bill.amountReceived < bill.amountRequested) {
            window.sessionStorage.removeItem('premiumBuyInProgress');
            clearInterval(checkInterval!);
            window.setTimeout(() => {
              completeOrder()
            }, 300000);
            toast({
              title: t('premiumAccount.buy.paymentFailed'),
              status: "error",
              duration: 300000,
              isClosable: true,
              position: "top",
              variant: "solid",
            });
          }
        } catch (error) {
          console.log(error);
          toast({
            title: t('premiumAccount.buy.paymentFailed'),
            status: "error",
            duration: 300000,
            isClosable: true,
            position: "top",
            variant: "solid",
          });
          window.sessionStorage.removeItem('premiumBuyInProgress');
          clearInterval(checkInterval!);
        }
      }, 5000);
    return () => {
      clearInterval(checkInterval);
    }
  }, [api, completeOrder, transactionId, t, toast]);  
  
  const handleCopy = () => {
    onCopy();
    toast({
      title: t('premiumAccount.buy.transactionIdCopied'),
      status: "success",
      duration: 2000,
      isClosable: true,
    });
  };

  return (
    <Box 
      display="flex" 
      flexDirection="column" 
      alignItems="center" 
      justifyContent="center" 
      p={6} 
    >
      <Spinner size="xl" color="brand.500" mb={4} />
      <Text fontSize="lg" fontWeight="semibold" mb={2}>
        {t('premiumAccount.buy.waitingForPayment')}
      </Text>
      <Flex alignItems="center" mt={4}>
        <Text fontSize="sm">
          {t('premiumAccount.buy.transactionId', { transactionId })}
        </Text>
        <Tooltip label={t('premiumAccount.buy.copyTransactionId')} fontSize="md">
          <IconButton
            ml={2}
            size="sm"
            icon={<MdContentCopy />}
            onClick={handleCopy}
            aria-label={t('premiumAccount.buy.copyTransactionId')}
            variant="outline"
          />
        </Tooltip>
      </Flex>
    </Box>
  );
};

