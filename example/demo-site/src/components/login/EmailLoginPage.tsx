import {  Container, Stack, Heading, } from '@chakra-ui/react'
import { signIn } from "next-auth/react";
import { useRouter } from 'next/navigation';
import { Logo } from '@/components/Logo';
import useTranslation from 'next-translate/useTranslation';
import EmailLogin from './EmailLogin';

export default function EmailLoginPage() {
  const router = useRouter()
  const { t } = useTranslation('accounts');

  const handleSignIn = async (email: string) => {
    signIn('email', { email, callbackUrl: '/account' });
  };

  return (<Container maxW="md" py={{ base: '12', md: '24' }}>
    <Stack spacing="8">
      <Stack spacing="6">
        <Logo />
        <Stack spacing={{ base: '2', md: '3' }} textAlign="center">
          <Heading size={{ base: 'xs', md: 'sm' }}>{t('loginTitle')}</Heading>
        </Stack>
      </Stack>
      <EmailLogin handleSignIn={handleSignIn} onReturn={() => {
        router.push('/login');
      }} />
    </Stack>

  </Container>);
};
