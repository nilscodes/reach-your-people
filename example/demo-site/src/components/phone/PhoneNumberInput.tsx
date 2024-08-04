import React, { useState } from 'react';
import { FormControl, FormLabel, Input, Select, Button, Stack, Text, Switch, Checkbox, FormErrorMessage } from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import Trans from 'next-translate/Trans';
import NextLink from '../NextLink';
import { useForm } from 'react-hook-form';
import { PhoneData } from './PhoneVerification';
import { formatPhoneNumber } from '@/lib/phoneutil';

type PhoneNumberInputProps = {
  countryCode: string;
  phoneNumber: string;
  onSubmit: (phoneData: PhoneData) => void
};

export default function PhoneNumberInput({ countryCode: initialCountryCode, phoneNumber: initialPhoneNumber, onSubmit }: PhoneNumberInputProps) {
  const [countryCode, setCountryCode] = useState(initialCountryCode); // Default to US
  const [phoneNumber, setPhoneNumber] = useState(initialPhoneNumber);
  const { t } = useTranslation('accounts');
  const {
    register,
    handleSubmit,
    formState: { errors },
} = useForm<PhoneData>();


  const handlePhoneNumberChange = (event: any) => {
    const { value } = event.target;
    setPhoneNumber(formatPhoneNumber(countryCode, value));
  };

  // Additional countries can be added here
  const countries = [{ code: '1', label: 'us' }]

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Stack spacing={4}>
        <FormControl id="countryCode" isRequired isInvalid={!!errors.countryCode}>
          <FormLabel>{t('country')}</FormLabel>
          <Select placeholder="Select country" defaultValue={countryCode} {...register('countryCode', { required: true })}>
            {countries.map((country) => (
              <option key={country.code} value={`+${country.code}`}>{t('countrySelectOption', { country: t(`countries.${country.label}`), code: country.code })}</option>
            ))}
          </Select>
          <FormErrorMessage>{errors.countryCode && t('errors.countryCodeRequired')}</FormErrorMessage>
        </FormControl>

        <FormControl id="phoneNumber" isRequired isInvalid={!!errors.phoneNumber}>
          <FormLabel>{t('phoneNumber')}</FormLabel>
          <Input
            type="tel"
            placeholder={t('phoneNumberPlaceholder')}
            {...register('phoneNumber', { required: true })}
            defaultValue={phoneNumber}
            onChange={handlePhoneNumberChange}
          />
          <FormErrorMessage>{errors.phoneNumber && t('errors.phoneNumberRequired')}</FormErrorMessage>
        </FormControl>
        <FormControl isRequired isInvalid={!!errors.consent}>
          <FormLabel>{t('phoneConsent')}</FormLabel>
            <Checkbox
              size='lg'
              spacing={4}
              alignItems='start'
              {...register('consent', { required: true })}
            >
              <Text fontSize="xs" color="fg.muted" align='justify'>
                <Trans i18nKey='accounts:phoneSubscriptionLegal' components={[
                  <NextLink key='link' href='/legal/terms' target='_blank' />,
                  <NextLink key='link' href='/legal/privacypolicy' target='_blank' />,
                ]} />
              </Text>
          </Checkbox>
          <FormErrorMessage>{errors.consent && t('errors.consentRequired')}</FormErrorMessage>
        </FormControl>

        <Button type="submit" width="full">
          {t('sendPhoneCode')}
        </Button>
        
      </Stack>
    </form>
  );
};
