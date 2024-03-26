import React, { useState } from 'react';
import { Button, Stack, useToast } from '@chakra-ui/react';
import PhoneNumberInput from './PhoneNumberInput';
import VerificationCodeInput from './VerificationCodeInput';
import { useApi } from '@/contexts/ApiProvider';
import { FaLeftLong } from 'react-icons/fa6';

type PhoneVerificationProps = {
    onReturn: () => void;
}

export default function PhoneVerification({ onReturn }: PhoneVerificationProps) {
  const [step, setStep] = useState('phoneNumberInput');
  const [countryCode, setCountryCode] = useState('+1');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [fullPhoneNumber, setFullPhoneNumber] = useState('');
  const toast = useToast();
  const api = useApi();

  const handlePhoneNumberSubmit = (countryCode: string, number: string) => {
    setPhoneNumber(number);
    setCountryCode(countryCode);
    const full = `${countryCode} ${number}`;
    setFullPhoneNumber(full);
    api.startPhoneVerification(full.replace(/[^+\d]/g, ''));
    setStep('verificationCodeInput');
  };

  const changePhoneNumber = () => {
    setStep('phoneNumberInput');
  }

  const handleVerificationSubmit = async (code: string) => {
    try {
      const verification = await api.verifyPhoneCode(fullPhoneNumber.replace(/[^+\d]/g, ''), code);
      if (verification === 'approved') {
        toast({
          title: 'Phone number verified successfully!',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        onReturn()
        return;
      }
    } catch (error) {
    }
    toast({
      title: 'The code was not correct.',
      description: 'Meep',
      status: 'error',
      duration: 5000,
      isClosable: true,
    });
  };

  return (
    <Stack spacing="3">      
      {step === 'phoneNumberInput' && <>
        <Button key="back"
          variant="secondary"
          leftIcon={<FaLeftLong />}
          cursor="pointer"
          onClick={onReturn}
        >
          Back to social logins
        </Button>
        <PhoneNumberInput countryCode={countryCode} phoneNumber={phoneNumber} onSubmit={handlePhoneNumberSubmit} />
      </>}
      {step === 'verificationCodeInput' && <VerificationCodeInput phoneNumber={fullPhoneNumber} onSubmit={handleVerificationSubmit} onGoBack={changePhoneNumber} />}
    </Stack>
  );
};

