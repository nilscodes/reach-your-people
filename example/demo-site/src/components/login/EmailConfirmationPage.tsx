import {  Container, Stack, Heading, Text, } from '@chakra-ui/react'
import { useRouter } from 'next/navigation';
import { Logo } from '@/components/Logo';
import useTranslation from 'next-translate/useTranslation';

export default function EmailConfirmationPage() {
  const { t } = useTranslation('accounts');

  return (<Container maxW="md" py={{ base: '12', md: '24' }}>
    <Stack spacing="8">
      <Stack spacing="6">
        <Logo />
        <Stack spacing={{ base: '2', md: '3' }} textAlign="center">
          <Heading size={{ base: 'xs', md: 'sm' }}>{t('emailConfirmationTitle')}</Heading>
        </Stack>
      </Stack>
      <Stack spacing="6">
        <Text>{t('emailConfirmationDescription')}</Text>
      </Stack>
    </Stack>
  </Container>);
};
