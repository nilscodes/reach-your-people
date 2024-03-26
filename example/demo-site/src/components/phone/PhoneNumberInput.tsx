import React, { useState } from 'react';
import { FormControl, FormLabel, Input, Select, Button, Stack, Text } from '@chakra-ui/react';

type PhoneNumberInputProps = {
  countryCode: string;
  phoneNumber: string;
  onSubmit: (countryCode: string, phoneNumber: string) => void
};

export default function PhoneNumberInput({ countryCode: initialCountryCode, phoneNumber: initialPhoneNumber, onSubmit }: PhoneNumberInputProps) {
  const [countryCode, setCountryCode] = useState(initialCountryCode); // Default to US
  const [phoneNumber, setPhoneNumber] = useState(initialPhoneNumber);

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

  return (
    <form onSubmit={handleSubmit}>
      <Stack spacing={4}>
        <FormControl>
          <FormLabel htmlFor="country">Country</FormLabel>
          <Select id="country" placeholder="Select country" value={countryCode} onChange={(e) => setCountryCode(e.target.value)} isReadOnly>
            <option value="+1">United States (+1)</option>
            {/* Additional countries can be added here */}
          </Select>
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="phone-number">Phone Number</FormLabel>
          <Input
            id="phone-number"
            type="tel"
            placeholder="Enter your phone number"
            value={phoneNumber}
            onChange={handlePhoneNumberChange}
          />
        </FormControl>

        <Button type="submit" width="full">
          Send Verification Code
        </Button>
        
        <Text fontSize="xs" color="fg.muted">
          By entering your number, you agree to our Terms of Service and Privacy Policy. Message and data rates may apply.
        </Text>
      </Stack>
    </form>
  );
};
