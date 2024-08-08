import {  Container, Stack, Button, } from '@chakra-ui/react'
import { useRouter } from 'next/navigation';
import useTranslation from 'next-translate/useTranslation';
import { LoginButton, TelegramAuthData } from '@telegram-auth/react';
import { FaLeftLong } from 'react-icons/fa6';

const botname = process.env.NEXT_PUBLIC_AUTH_TELEGRAM_BOT_NAME ?? '';

type TelegramLoginProps = {
  onReturn: () => void;
  handleSignIn: (data: TelegramAuthData) => void;
}

export default function TelegramLogin({
  onReturn, handleSignIn,
}: TelegramLoginProps) {
  const router = useRouter()
  const { t } = useTranslation('accounts');

  return (<Stack spacing="3">
    <Button key="back"
      variant="secondary"
      leftIcon={<FaLeftLong />}
      cursor="pointer"
      onClick={onReturn}
    >
      {t('backToSocial')}
    </Button>
    <Container>
      <LoginButton
          botUsername={botname}
          onAuthCallback={handleSignIn}
      />
    </Container>
  </Stack>);
};
