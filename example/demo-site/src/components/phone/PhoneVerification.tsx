import React, { useState } from 'react';
import { Button, Stack, useToast } from '@chakra-ui/react';
import PhoneNumberInput from './PhoneNumberInput';
import VerificationCodeInput from './VerificationCodeInput';
import { useApi } from '@/contexts/ApiProvider';
import { FaLeftLong } from 'react-icons/fa6';
import useTranslation from 'next-translate/useTranslation';

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
  const { t } = useTranslation('accounts');

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
    return new Promise<void>(async (resolve, reject) => {
      try {
        const verification = await api.verifyPhoneCode(fullPhoneNumber.replace(/[^+\d]/g, ''), code);
        if (verification === 'approved') {
          toast({
            title: t('phoneVerified'),
            status: 'success',
            duration: 5000,
            isClosable: true,
          });
          onReturn()
          resolve();
          return;
        }
      } catch (error) {
      }
      toast({
        title: t('phoneCodeIncorrect'),
        description: t('phoneCodeIncorrectDescription'),
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
      reject(t('phoneCodeIncorrect'));      
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
          {t('backToSocial')}
        </Button>
        <PhoneNumberInput countryCode={countryCode} phoneNumber={phoneNumber} onSubmit={handlePhoneNumberSubmit} />
      </>}
      {step === 'verificationCodeInput' && <VerificationCodeInput phoneNumber={fullPhoneNumber} onSubmit={handleVerificationSubmit} onGoBack={changePhoneNumber} />}
    </Stack>
  );
};

