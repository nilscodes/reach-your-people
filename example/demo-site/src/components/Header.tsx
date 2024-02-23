'use client';

import {
  Avatar,
  Box,
  Button,
  ButtonGroup,
  Container,
  HStack,
} from '@chakra-ui/react'
import { useSession } from "next-auth/react";
import { Logo } from '@/components/Logo'
import AuthButton from './AuthButton';
import NextLink from './NextLink';

export default function Header() {
  const { data: session } = useSession();

  return (<Box borderBottomWidth="1px" bg="bg.surface" position="relative" zIndex="tooltip">
    <Container py="4">
      <HStack justify="space-between" spacing="8">
        <HStack spacing="10">
          <HStack spacing="3">
            <NextLink href="/"><Logo h="48px" w="192px" /></NextLink>
          </HStack>
          <ButtonGroup
            size="lg"
            variant="text"
            colorScheme="gray"
            spacing="8"
            display={{ base: 'none', lg: 'flex' }}
          >
            <NextLink href="/test"><Button>Verify NFT Project Publisher</Button></NextLink>
          </ButtonGroup>
        </HStack>
        <HStack spacing={{ base: '2', md: '4' }}>
          <AuthButton />
          {session?.user?.image && (<Avatar boxSize="10" src={session?.user?.image} />)}
        </HStack>
      </HStack>
    </Container>
  </Box>);
};
