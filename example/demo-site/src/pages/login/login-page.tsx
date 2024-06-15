import {
  Button,
  Container,
  Heading,
  Stack,
} from '@chakra-ui/react'
import { CardanoIcon, providersConfig } from '@/components/ProviderIcons';
import { FaCheck } from 'react-icons/fa';
import { signIn } from "next-auth/react";
import { useRouter } from 'next/navigation';
import { Logo } from '@/components/Logo';
import useTranslation from 'next-translate/useTranslation';

export default function LoginPage() {
  const router = useRouter();
  const { t } = useTranslation('accounts');

  const handleSignIn = (provider: string) => {
    signIn(provider, { callbackUrl: '/account' });
  };

  const linkedProviders: string[] = []; // accounts.map((account) => account.provider);

  return (<Container maxW="md" py={{ base: '12', md: '24' }}>
    <Stack spacing="8">
      <Stack spacing="6">
        <Logo />
        <Stack spacing={{ base: '2', md: '3' }} textAlign="center">
          <Heading size={{ base: 'xs', md: 'sm' }}>{t('loginTitle')}</Heading>
        </Stack>
      </Stack>
      <Stack spacing="6">
        {/* <Stack spacing="4" pt="4">
          <FormControl>
            <Input id="email" type="email" placeholder=" " data-peer />
            <FormLabel htmlFor="email" variant="floating">
              Email
            </FormLabel>
          </FormControl>
          <Button>Continue with email</Button>
        </Stack>
        <HStack>
          <Divider />
          <Text textStyle="sm" color="fg.muted">
            OR
          </Text>
          <Divider />
        </HStack> */}
        <Stack spacing="3">
          <Button key="cardano"
            variant="secondary"
            leftIcon={<CardanoIcon />}
            cursor="pointer"
            onClick={() => {
              router.push('/login/cardano');
            }}
          >
            {t('cardanoWallet')}
          </Button>
          {providersConfig.map((provider) => {
              const isLinked = linkedProviders.includes(provider.id);
              return (
                <Button key={provider.id}
                  disabled={isLinked}
                  variant="secondary"
                  leftIcon={provider.Component && provider.Component}
                  rightIcon={isLinked ? <FaCheck /> : <></>}
                  cursor={isLinked ? "not-allowed" : "pointer"}
                  onClick={() => {
                    if(!isLinked) {
                      handleSignIn(provider.id);
                    }
                  }}
                >
                  {provider.name}
                </Button>
              );
            })}
        </Stack>
      </Stack>
    </Stack>
  </Container>);
};
