import React, { useEffect, useState } from 'react';
import { Button, Stack, Text, useToast } from '@chakra-ui/react';
import { useApi } from '@/contexts/ApiProvider';
import { FaLeftLong } from 'react-icons/fa6';
import useTranslation from 'next-translate/useTranslation';

type PushApiVerificationProps = {
    onReturn: () => void;
}

export default function PushApiVerification({ onReturn }: PushApiVerificationProps) {
  const toast = useToast();
  const api = useApi();
  const { t } = useTranslation('accounts');

  useEffect(() => {
    if ('serviceWorker' in navigator && 'PushManager' in window) {
      navigator.serviceWorker.register('/sw.js').then(function(swReg) {
        console.log('Service Worker is registered', swReg);
      }).catch(function(error) {
        console.error('Service Worker Error', error);
      });
    }
  }, []);
  
  const requestPushNotifications = async () => {
    const permission = await Notification.requestPermission();
    if (permission === 'granted') {
      const registration = await navigator.serviceWorker.ready;
      const newSubscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY
      });
      await api.linkPushApiSubscription(newSubscription, t('pushApi'));
      toast({
        title: t('pushNotificationsEnabled'),
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      onReturn();
    } else {
      toast({
        title: t('pushNotificationsDenied'),
        description: t('pushNotificationsDeniedDescription'),
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  return (
    <Stack spacing="3">      
        <Button key="back"
          variant="secondary"
          leftIcon={<FaLeftLong />}
          cursor="pointer"
          onClick={onReturn}
        >
          {t('backToSocial')}
        </Button>
        <Stack spacing={4}>
          <Text>
            {t('pushApiVerificationInfo')}
          </Text>

          <Button width="full" onClick={requestPushNotifications}>
            {t('enableBrowserNotifications')}
          </Button>
          
          <Text fontSize="xs" color="fg.muted">
            {t('browserNotificationsLegal')}
          </Text>
        </Stack>
    </Stack>
  );
};

