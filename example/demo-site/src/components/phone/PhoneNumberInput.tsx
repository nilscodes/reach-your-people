import React, { useState } from 'react';
import { FormControl, FormLabel, Input, Select, Button, Stack, Text, Switch, Checkbox, FormErrorMessage } from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import Trans from 'next-translate/Trans';
import NextLink from '../NextLink';
import { useForm } from 'react-hook-form';
import { PhoneData } from './PhoneVerification';

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
    // Simplified formatting, replace with a more robust solution as needed
    const formattedNumber = value.replace(/\D/g, '').substring(0, 10); // Remove non-digits and limit length
    // Style with the US format
    const usNumber = `(${formattedNumber.substring(0, 3)}) ${formattedNumber.substring(3, 6)} ${formattedNumber.substring(6)}`
    setPhoneNumber(usNumber);
  };

  // Additional countries can be added here
  const countries = [{ code: '1', label: 'us' }]

  console.log(errors);

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
                  <NextLink key='link' href='https://app.termly.io/document/terms-of-use-for-saas/7a266cd3-f4f6-464e-8e0a-28f7a07ba7e0' target='_blank' />,
                  <NextLink key='link' href='https://app.termly.io/document/privacy-policy/03f7e652-321e-4bc6-a043-a7880d90b223' target='_blank' />,
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
