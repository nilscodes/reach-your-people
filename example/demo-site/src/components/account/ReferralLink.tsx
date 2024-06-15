import React, { useState, useEffect, useCallback, useRef } from 'react';
import {
  Box,
  Button,
  Container,
  FormControl,
  FormLabel,
  Heading,
  Input,
  Skeleton,
  Stack,
  Text,
  useToast
} from '@chakra-ui/react';
import { MdContentCopy } from 'react-icons/md';
import { useApi } from '@/contexts/ApiProvider';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';

interface ReferralLinkProps {
  accountSettings: Record<string, string>;
}

export default function ReferralLink({ accountSettings }: ReferralLinkProps) {
  const [referralUrl, setReferralUrl] = useState<string | null>(accountSettings.REFERRAL_URL);
  const apiCallMadeRef = useRef(false);
  const toast = useToast();
  const api = useApi()
  const { t } = useTranslation('accounts');

  const generateReferralUrl = useCallback(async () => {
    try {
      const url = await api.generateReferralUrl();
      setReferralUrl(url);
    } catch (error) {
      console.error('Error generating referral URL:', error);
      toast({
        title: 'Error',
        description: 'Failed to generate referral URL',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }, []);

  useEffect(() => {
    if (!referralUrl) {
      if (!apiCallMadeRef.current) { // Avoid duplicate referral URL generation due to React strict mode
        generateReferralUrl();
        apiCallMadeRef.current = true;
      }
    }
  }, []);

  const copyToClipboard = () => {
    if (referralUrl) {
      navigator.clipboard.writeText(referralUrl).then(() => {
        toast({
          title: 'Copied',
          description: 'Referral URL copied to clipboard',
          status: 'success',
          duration: 2000,
          isClosable: true,
        });
      });
    }
  };

  return (
    <Card heading={t('referralTitle')} description={t('referralDescription')}>
      {/* <Stack spacing="3" direction={{ base: 'column', sm: 'row' }}>
        <Button variant="secondary" leftIcon={<FacebookIcon />} iconSpacing="3">
          Facebook
        </Button>
        <Button variant="secondary" leftIcon={<TwitterIcon />} iconSpacing="3">
          Twitter
        </Button>
        <Button variant="secondary" leftIcon={<WhatsAppIcon />} iconSpacing="3">
          WhatsApp
        </Button>
      </Stack> */}
      <Stack direction={{ base: 'column', sm: 'row' }} spacing="3">
        {referralUrl ? (<>
          <FormControl id="email" width={{ sm: 'sm' }}>
            <FormLabel>{t('referralLink')}</FormLabel>
            <Input type="text" value={referralUrl} maxW={{ sm: 'sm' }} readOnly />
          </FormControl>
          <Button alignSelf={{ sm: 'flex-end' }}  onClick={copyToClipboard}><MdContentCopy /></Button>
        </>) : (
          <Skeleton h="10" w="full" />
        )}
      </Stack>
    </Card>);
};
