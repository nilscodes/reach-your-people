import { Button, Stack, Img, Heading, FormControl, Input } from '@chakra-ui/react'
import { Wallet } from '@meshsdk/core'
import useTranslation from 'next-translate/useTranslation';
import { useState } from 'react';
import { FaLeftLong } from 'react-icons/fa6';

type EmailLoginProps = {
  onReturn: () => void;
  handleSignIn: (email: string) => void;
}

export default function EmailLogin({
  onReturn, handleSignIn,
}: EmailLoginProps) {
  const [email, setEmail] = useState('');
  const { t } = useTranslation('accounts');

  const submitSignIn = () => {
    handleSignIn(email);
  }

  return(<Stack spacing="3">
    <Button key="back"
      variant="secondary"
      leftIcon={<FaLeftLong />}
      cursor="pointer"
      onClick={onReturn}
    >
      {t('backToSocial')}
    </Button>
    <FormControl id="email">
      <Input type="email" placeholder="Email" onChange={(e) => {
        setEmail(e.target.value);
      }} />
    </FormControl>
    <Button onClick={() => submitSignIn()}>Continue with email</Button>
  </Stack>);
}