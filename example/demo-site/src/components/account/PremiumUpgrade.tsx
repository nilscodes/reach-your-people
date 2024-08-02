import React, { useEffect, useState } from 'react';
import { 
  useDisclosure,
  Text,
  Box,
  SimpleGrid,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import ActionButton from './ActionButton';
import { AiOutlineStar } from 'react-icons/ai'
import { GiCat } from 'react-icons/gi'
import PricingCard from './PricingCard';
import PaymentWaiting from './PaymentWaiting';
import PaymentModal from './PaymentModal';

export default function PremiumUpgrade({ completeOrder }: { completeOrder: () => void }) {
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

  if (waitingForTransaction) {
    return (<Box>
      <PaymentWaiting transactionId={waitingForTransaction} completeOrder={completeOrder} />
    </Box>);
  } else {
    return (<Box>
      <SimpleGrid
        columns={{ base: 1, lg: 2 }}
        spacing={{ base: '8', lg: '0' }}
        mx={{ base: 'auto', lg: '8' }}
        justifyItems="center"
        alignItems="center"
      >
        <PricingCard
          data={{
            price: <Text>{t('premiumAccount.levels.basic.price')}</Text>,
            name: t('premiumAccount.levels.basic.title'),
            features: [
              t('premiumAccount.features.privacy'),
              t('premiumAccount.features.projectAnnouncements'),
              t('premiumAccount.features.stakepoolAnnouncements'),
              t('premiumAccount.features.drepAnnouncements'),
            ],
          }}
          icon={AiOutlineStar}
        />
        <PricingCard
          zIndex={1}
          isPopular
          transform={{ lg: 'scale(1.05)' }}
          data={{
            price: <Text>{t('premiumAccount.levels.premium.price')}</Text>,
            priceSub: <>
                        <Text>{t('premiumAccount.levels.premium.priceSub1')}</Text>
                        <Text>{t('premiumAccount.levels.premium.or')}</Text>
                        <Text>{t('premiumAccount.levels.premium.priceSub2')}</Text>
                      </>,
            name: t('premiumAccount.levels.premium.title'),
            features: [
              t('premiumAccount.features.allFree'),
              t('premiumAccount.features.customLink'),
              t('premiumAccount.features.earlySupporterAchievement'),
              t('premiumAccount.features.noAds'),
              t('premiumAccount.features.walletNotifications'),
              t('premiumAccount.features.instantUpdates'),
            ],
          }}
          icon={GiCat}
          button={
            <ActionButton onClick={() => onOpen()}>
              {t('premiumAccount.buy.ctaButton')}
            </ActionButton>
          }
        />
      </SimpleGrid>
      <PaymentModal isOpen={isOpen} completeOrder={completePremiumOrder} paymentAborted={paymentAborted} header={t('premiumAccount.buy.header')} />
    </Box>);
  }
}
