import React, { useEffect, useState } from 'react';
import { 
  Box,
  Button,
  Heading,
  Icon,
  SimpleGrid,
  Square,
  Stack,
  Text,
  useDisclosure,
  VStack,
} from '@chakra-ui/react';
import { Account } from '../../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import PaymentModal from './PaymentModal';
import PaymentWaiting from './PaymentWaiting';
import { BsStars } from 'react-icons/bs';
import { MdOutlineShare } from 'react-icons/md';

type PremiumInfoProps = {
  account: Account;
  completeOrder: () => void
};

const benefits = [
  {
    name: 'premiumAccount.features.noAds',
    description: 'premiumAccount.features.noAdsDescription',
    icon: BsStars,
  },
  {
    name: 'premiumAccount.features.customLink',
    description: 'premiumAccount.features.customLinkDescription',
    icon: MdOutlineShare,
  },
];

export default function PremiumInfo({ account, completeOrder }: PremiumInfoProps) {
  const { t } = useTranslation('accounts');
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [waitingForTransaction, setWaitingForTransaction] = useState<string | null>(null);

  useEffect(() => {
    setWaitingForTransaction(window.sessionStorage.getItem('premiumBuyInProgress'));
  }, []);

  const paymentAborted = () => {
    onClose();
  }

  const completePremiumOrder = (transactionId: string) => {
    window.sessionStorage.setItem('premiumBuyInProgress', transactionId);
    setWaitingForTransaction(transactionId);
    onClose();
  }

  const premiumUntil = new Date(account.premiumUntil!)

  if (waitingForTransaction) {
    return (<Box>
      <PaymentWaiting transactionId={waitingForTransaction} completeOrder={completeOrder} />
    </Box>);
  } else {
    return (<Stack spacing="8"
      maxW="3xl"
      mx="auto"
      px="0"
      py={{ base: '6', md: '8' }}
    >
      <Box
        mx="auto"
        px={{ base: '4', md: '6' }}
        py={{ base: '5', md: '6' }}
        bg="bg.surface"
        borderRadius="lg"
        boxShadow="sm"
      >
        <VStack spacing={6}>
          <Stack>
            <Text textStyle="sm" color="fg.muted">
              {t('premiumAccount.premiumUntil')}
            </Text>
            <Heading size={{ base: 'sm', md: 'md' }}>{premiumUntil.toDateString()}</Heading>
          </Stack>
          <Button variant="outline" colorScheme="brand" onClick={() => onOpen()}>
            {t('premiumAccount.extend')}
          </Button>
        </VStack>
      </Box>
      <Heading size="lg">{t('premiumAccount.benefitsHeader')}</Heading>
      <SimpleGrid columns={{ base: 1, md: 2 }} columnGap={8} rowGap={{ base: 10, md: 16 }}>
          {benefits.map((benefit) => (
            <Stack key={benefit.name} spacing={{ base: '4', md: '5' }}>
              <Square
                size={{ base: '10', md: '12' }}
                bg="accent"
                color="fg.inverted"
                borderRadius="lg"
              >
                <Icon as={benefit.icon} boxSize={{ base: '5', md: '6' }} />
              </Square>
              <Stack spacing={{ base: '1', md: '2' }} flex="1">
                <Text fontSize={{ base: 'lg', md: 'xl' }} fontWeight="medium">
                  {t(benefit.name)}
                </Text>
                <Text color="fg.muted">{t(benefit.description)}</Text>
              </Stack>
              {/* <Button
                variant="text"
                colorScheme="blue"
                rightIcon={<FiArrowRight />}
                alignSelf="start"
              >
                Read more
              </Button> */}
            </Stack>
          ))}
        </SimpleGrid>
      <PaymentModal isOpen={isOpen} completeOrder={completePremiumOrder} paymentAborted={paymentAborted} header={t('premiumAccount.buy.headerExtend')} />
    </Stack>);
  }
}
