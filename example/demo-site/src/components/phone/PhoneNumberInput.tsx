import React, { useState } from 'react';
import { FormControl, FormLabel, Input, Select, Button, Stack, Text } from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';

type PhoneNumberInputProps = {
  countryCode: string;
  phoneNumber: string;
  onSubmit: (countryCode: string, phoneNumber: string) => void
};

export default function PhoneNumberInput({ countryCode: initialCountryCode, phoneNumber: initialPhoneNumber, onSubmit }: PhoneNumberInputProps) {
  const [countryCode, setCountryCode] = useState(initialCountryCode); // Default to US
  const [phoneNumber, setPhoneNumber] = useState(initialPhoneNumber);
  const { t } = useTranslation('accounts');

  const handlePhoneNumberChange = (event: any) => {
    const { value } = event.target;
    // Simplified formatting, replace with a more robust solution as needed
    const formattedNumber = value.replace(/\D/g, '').substring(0, 10); // Remove non-digits and limit length
    // Style with the US format
    const usNumber = `(${formattedNumber.substring(0, 3)}) ${formattedNumber.substring(3, 6)} ${formattedNumber.substring(6)}`
    setPhoneNumber(usNumber);
  };

  const handleSubmit = (event: any) => {
    event.preventDefault();
    // Here, you might want to validate the phone number before submitting
    onSubmit(countryCode, phoneNumber);
  };

  // Additional countries can be added here
  const countries = [{ code: '1', label: 'us' }]

  return (
    <form onSubmit={handleSubmit}>
      <Stack spacing={4}>
        <FormControl>
          <FormLabel htmlFor="country">{t('country')}</FormLabel>
          <Select id="country" placeholder="Select country" value={countryCode} onChange={(e) => setCountryCode(e.target.value)} isReadOnly>
            {countries.map((country) => (
              <option key={country.code} value={`+${country.code}`}>{t('countrySelectOption', { country: t(`countries.${country.label}`), code: country.code })}</option>
            ))}
          </Select>
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="phone-number">{t('phoneNumber')}</FormLabel>
          <Input
            id="phone-number"
            type="tel"
            placeholder={t('phoneNumberPlaceholder')}
            value={phoneNumber}
            onChange={handlePhoneNumberChange}
          />
        </FormControl>

        <Button type="submit" width="full">
          {t('sendPhoneCode')}
        </Button>
        
        <Text fontSize="xs" color="fg.muted">
          {t('phoneSubscriptionLegal')}
        </Text>
      </Stack>
    </form>
  );
};
