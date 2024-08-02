import React, { useState, useEffect, useCallback, useRef } from 'react';
import {
  Button,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Input,
  InputGroup,
  InputLeftAddon,
  Skeleton,
  Stack,
  useToast,
  VStack
} from '@chakra-ui/react';
import { MdCancel, MdCheck, MdContentCopy, MdEdit, MdSave } from 'react-icons/md';
import { useApi } from '@/contexts/ApiProvider';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';
import { Account } from '@/lib/ryp-subscription-api';
import { isPremiumAccount } from '@/lib/premium';
import { useForm } from 'react-hook-form';

interface ReferralLinkProps {
  account: Account;
  accountSettings: Record<string, string>;
}

type PremiumReferralShortcode = {
  shortcode: string
}

export default function ReferralLink({ account, accountSettings }: ReferralLinkProps) {
  const isPremium = isPremiumAccount(account)
  const useUrl = isPremium && accountSettings.REFERRAL_URL_PREMIUM ? accountSettings.REFERRAL_URL_PREMIUM : accountSettings.REFERRAL_URL;
  const [referralUrl, setReferralUrl] = useState<string | null>(useUrl);
  const [currentShortcode, setCurrentShortcode] = useState<string>(referralUrl?.split('/').pop() || '');
  const [editing, setEditing] = useState(false);
  const apiCallMadeRef = useRef(false);
  const toast = useToast();
  const api = useApi()
  const { t } = useTranslation('accounts');
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PremiumReferralShortcode>();

  const generateReferralUrl = useCallback(async () => {
    try {
      const url = await api.generateReferralUrl();
      setReferralUrl(url);
    } catch (error) {
      console.error('Error generating referral URL:', error);
      toast({
        description: t('referralLinkGenerationError'),
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }, []);

  const updatePremiumShortcode = async (data: PremiumReferralShortcode) => {
    try {
      const newReferralUrl = await api.updateReferralShortcode(data.shortcode);
      toast({
        description: t('referralShortcodeUpdated'),
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      setReferralUrl(newReferralUrl);
      setCurrentShortcode(newReferralUrl?.split('/').pop() || '');
      setEditing(false);
    } catch (error: any) {
      console.error('Error updating referral shortcode:', error);
      const errorCode = error.response?.status;
      const errorMessage = errorCode === 400 ? t('errors.premiumShortcodeProfanity') : t('errors.referralShortcodeInUse');
      toast({
        description: errorMessage,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }

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
          description: t('referralLinkCopied'),
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
      <VStack spacing="6" alignItems="start">
        {referralUrl ? (<>
          {!editing && (<Stack direction={{ base: 'column', sm: 'row' }} spacing="3">
            <FormControl id="referralLink" width={{ sm: 'sm' }}>
              <FormLabel>{t('referralLink')}</FormLabel>
              <Input type="text" value={referralUrl} maxW={{ sm: 'sm' }} readOnly />
            </FormControl>
            {isPremium && <Button alignSelf={{ sm: 'flex-end' }} variant="ghost" onClick={() => setEditing(true)} aria-label='edit'><MdEdit /></Button>}
            <Button alignSelf={{ sm: 'flex-end' }} onClick={copyToClipboard} aria-label='copy to clipboard'><MdContentCopy /></Button>
          </Stack>)}
          {editing && (<Stack direction={{ base: 'column', sm: 'row' }} spacing="3">
            <FormControl id="shortcode" width={{ sm: 'sm' }} isInvalid={!!errors.shortcode}>
              <FormLabel>{t('referralLink')}</FormLabel>
              <FormErrorMessage mb="3">{errors.shortcode && t('errors.premiumShortcodeRestrictions')}</FormErrorMessage>
              <InputGroup maxW={{ md: 'xl' }}>
                <InputLeftAddon>https://go.ryp.io/</InputLeftAddon>
                <Input type="text" maxW={{ sm: 'sm' }} maxLength={10} defaultValue={currentShortcode} placeholder={t('referralShortcodePlaceholder')} {...register('shortcode', { required: true, pattern: /^[A-Za-z0-9]{4,10}$/i })} />
              </InputGroup>
            </FormControl>
            <Button alignSelf={{ sm: 'flex-end' }} variant="ghost" onClick={() => setEditing(false)} aria-label='cancel editing'><MdCancel /></Button>
            <Button alignSelf={{ sm: 'flex-end' }} onClick={handleSubmit(updatePremiumShortcode)} aria-label='save changes'><MdCheck /></Button>
          </Stack>)}
        </>) : (
          <Skeleton h="10" w="full" />
        )}
      </VStack>
    </Card>);
};
