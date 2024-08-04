import { useState } from 'react';
import { Container, Stack, Heading, Text, Button, Input, useToast, Box } from '@chakra-ui/react';
import { useSearchParams } from 'next/navigation';
import { Logo } from '@/components/Logo';
import useTranslation from 'next-translate/useTranslation';
import { useApi } from '@/contexts/ApiProvider';
import NextLink from '../NextLink';

export default function EmailUnsubscribePage() {
  const searchParams = useSearchParams();
  const email = searchParams.get('email') as string;
  const { t } = useTranslation('accounts');
  const [isUnsubscribed, setIsUnsubscribed] = useState(false);
  const api = useApi();
  const toast = useToast();

  const handleUnsubscribe = async () => {
    try {
      await api.unsubscribe(email);
      setIsUnsubscribed(true);
    } catch (error) {
      toast({
        title: t('unsubscribeError'),
        description: <Box my="6">
          <NextLink href='https://discord.gg/nzka3K2WUS' isExternal><Button variant="link" color='black' size="lg">{t('supportLink')}</Button></NextLink>
        </Box>,
        position: 'top',
        status: 'error',
        duration: 300000,
        isClosable: true,
      });
    }
  };

  return (
    <Container maxW="md" py={{ base: '12', md: '24' }}>
      <Stack spacing="8">
        <Stack spacing="6">
          <Logo />
          <Stack spacing={{ base: '2', md: '3' }} textAlign="center">
            <Heading size={{ base: 'xs', md: 'sm' }}>
              {isUnsubscribed ? t('unsubscribeSuccessTitle') : t('unsubscribeTitle')}
            </Heading>
          </Stack>
        </Stack>
        {isUnsubscribed ? (
          <Text>{t('unsubscribeSuccessMessage')}</Text>
        ) : (
          <Stack spacing="6">
            <Text>{t('unsubscribeDescription')}</Text>
            <Text fontWeight="bold">{email}</Text>
            <Button colorScheme="red" onClick={handleUnsubscribe}>
              {t('unsubscribeButton')}
            </Button>
          </Stack>
        )}
      </Stack>
    </Container>
  );
}
