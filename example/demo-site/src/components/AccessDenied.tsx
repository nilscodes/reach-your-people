import React from 'react';
import { Container, Heading, Text, Button, Box, VStack } from '@chakra-ui/react';
import { useRouter } from 'next/router';
import accessDenied from '../../public/access-denied.webp';
import Image from './Image';

export default function AccessDenied() {
  const router = useRouter();

  return (
    <Container centerContent>
      <Box textAlign="center" py={10} px={6}>
        <VStack spacing={4}>
          <Image
            boxSize="300px"
            src={accessDenied}
            alt="Access Denied"
            mb={4}
          />
          <Text color={'gray.500'}>
            Sorry, you do not have access to this page.
          </Text>
          <Button
            variant="solid"
            mt={4}
            onClick={() => router.push('/')}
          >
            Jump Home
          </Button>
        </VStack>
      </Box>
    </Container>
  );
};
