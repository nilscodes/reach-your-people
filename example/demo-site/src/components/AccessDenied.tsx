import React from 'react';
import { Container, Text, Button, Box, VStack } from '@chakra-ui/react';
import { useRouter } from 'next/router';
import accessDenied from '../../public/access-denied.webp';
import Image from './Image';
import Head from 'next/head';
import useTranslation from 'next-translate/useTranslation';

export default function AccessDenied() {
  const router = useRouter();
  const { t } = useTranslation('common');

  return (<>
     <Head>
        <title>{t('accessDenied.title')}</title>
     </Head>
    <Container centerContent>
      <Box textAlign="center" py={10} px={6}>
        <VStack spacing={4}>
          <Image
            boxSize="300px"
            src={accessDenied}
            alt={t('accessDenied.title')}
            mb={4}
          />
          <Text color={'gray.500'}>
            {t('accessDenied.message')}
          </Text>
          <Button
            variant="solid"
            mt={4}
            onClick={() => router.push('/')}
          >
            {t('accessDenied.cta')}
          </Button>
        </VStack>
      </Box>
    </Container>
  </>);
};
