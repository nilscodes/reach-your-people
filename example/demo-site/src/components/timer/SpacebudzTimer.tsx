import { Box, Button, Icon, LightMode, Stack, Text, useColorModeValue } from '@chakra-ui/react'
import { VscChromeClose } from 'react-icons/vsc'
import { Timer } from './Timer'
import NextLink from '../NextLink';

export const SpacebudzTimer = () => {
  const spacebudzRaffleTime = new Date('2024-07-31T23:59:59Z').getTime();
  return (<Box as="section">
    <Box bg={useColorModeValue('black', 'gray.700')} color="white" position="relative">
      <Box maxW="7xl" mx="auto" px={{ base: '4', md: '8', lg: '12' }} py={{ base: '3', md: '2.5' }}>
        <Stack
          direction={{ base: 'column', md: 'row' }}
          align="center"
          justify="center"
          spacing={{ base: '2', md: '20', lg: '7.5rem' }}
        >
          <Text fontWeight="medium" fontSize="xl">
            Your chance to win a Spacebudz NFT, simply by signing up!
          </Text>
          <Timer expiresInSeconds={spacebudzRaffleTime} />
          <LightMode>
            <NextLink href="/login">
              <Button
                bg="white"
                color="black"
                px="12"
                display={{ base: 'none', md: 'inline-block' }}
                _focus={{ boxShadow: 'none' }}
                _focusVisible={{ boxShadow: 'outline' }}
              >
                Sign up
              </Button>
            </NextLink>
          </LightMode>
          {/* <Box
            as="button"
            aria-label="Close banner"
            position="absolute"
            right={{ base: '2', md: '4', lg: '6' }}
            top={{ base: '0', md: 'unset' }}
          >
            <Icon as={VscChromeClose} boxSize={{ base: '5', md: '6' }} />
          </Box> */}
        </Stack>
      </Box>
    </Box>
  </Box>)
}