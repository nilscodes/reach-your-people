import React, { useState, useRef, useEffect } from 'react';
import { Button, HStack, Input, Text, VStack, useToast } from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';

type VerificationCodeInputProps = {
  phoneNumber: string;
  onGoBack: () => void;
  onSubmit: (code: string) => Promise<void>;
};

export default function VerificationCodeInput({ phoneNumber, onGoBack, onSubmit }: VerificationCodeInputProps) {
  const toast = useToast();
  const { t } = useTranslation('accounts');
  const [code, setCode] = useState(new Array(6).fill(''));
  const [isSending, setIsSending] = useState(false); // Add a loading state for the button
  const inputsRef = useRef<(HTMLInputElement | null)[]>([]);
  const isCodeComplete = code.every((digit) => digit.trim() !== '');

  // Automatically focus the next input field after a digit is entered
  const focusNextInput = (index: number, value: string) => {
    if (index < 5 && value) {
      inputsRef.current[index + 1]?.focus();
    }
    if (index === 5 && value) {
      inputsRef.current[index]?.blur();
    }

    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);

    const fullCode = newCode.join('');
    if (fullCode.length === 6 && index === 5) {
      sendCode(fullCode);
    }
  };

  const sendCode = (fullCode: string) => {
    setIsSending(true);
    onSubmit(fullCode).finally(() => setIsSending(false));
  };

  useEffect(() => {
    inputsRef.current[0]?.focus();
  }, []);

  const handleChange = (value: string, index: number) => {
    if (/^[0-9]$/.test(value)) {
      focusNextInput(index, value);
    } else if (value === '') {
      focusNextInput(index, '');
    } else {
      toast({
        title: t('codeInvalidDigit'),
        description: t('codeInvalidDigitDescription'),
        status: 'error',
        duration: 2000,
        isClosable: true,
      });
    }
  };

  return (
    <VStack spacing="3">
      <Text mb="4">{t('verifyingCode', { phoneNumber })}
        <Button variant="link" colorScheme="blue" onClick={onGoBack} ml="4">
          {t('changePhoneNumber')}
        </Button>
      </Text>
      
      <HStack spacing="2">
        {code.map((digit, index) => (
          <Input
            key={index}
            ref={(el) => (inputsRef.current[index] = el)}
            value={digit}
            onChange={(e) => handleChange(e.target.value, index)}
            type="number"
            maxLength={1}
            textAlign="center"
            size="lg"
          />
        ))}
      </HStack>
      <Button isDisabled={!isCodeComplete && !isSending} width="full" onClick={() => sendCode(code.join(''))} isLoading={isSending}>
        {t('sendVerificationCode')}
      </Button>
    </VStack>
  );
};
