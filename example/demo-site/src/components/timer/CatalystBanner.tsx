import { Box, Button,  Container, Icon, Link, Square, Stack, Text, useBreakpointValue, useColorModeValue as mode, IconButton } from '@chakra-ui/react'
import { IoIosBulb } from 'react-icons/io';
import { IoClose } from 'react-icons/io5';



export default function CatalystBanner({ onClose }: { onClose: () => void }) {
    const isMobile = useBreakpointValue({ base: true, md: false })
  
    return (<Box bg={mode('gray.300', 'gray.300')} color='black' boxShadow={mode('sm', 'sm')} maxW="7xl" mx={{ base: '8', xl: 'auto' }} rounded='xl' zIndex="9" my='4'>
      <Container py={{ base: '4', lg: '1.5' }} position='relative' maxW="7xl">
        <Stack
          direction={{ base: 'column', sm: 'row' }}
          justify="space-between"
          spacing={{ base: '3', md: '6' }}
          alignItems='center'
        >
          <Stack
            spacing="4"
            direction='row'
            align={{ base: 'start', md: 'center' }}
          >
            {!isMobile && (
              <Square size="12" bg="bg-subtle" borderRadius="md">
                <Icon as={IoIosBulb} boxSize="6" />
              </Square>
            )}
            <Stack
              direction={{ base: 'column', md: 'row' }}
              spacing={{ base: '0.5', md: '1.5' }}
              pe={{ base: '4', sm: '0' }}
            >
              <Text fontWeight="medium">Do you want to be notified of your wallet activity, your stakepool retiring or what your dRep votes for? Vote for our proposals in Catalyst Fund 12. Simply search for <b>RYP</b> and make it a reality.</Text>
            </Stack>
            {isMobile && (<IconButton aria-label="Close Catalyst Banner" icon={<IoClose />} onClick={onClose} variant="ghost" color="gray.800" />)}
          </Stack>
          <Stack
            direction={{ base: 'column', sm: 'row' }}
            spacing={{ base: '3', sm: '2' }}
            align={{ base: 'stretch', sm: 'center' }}
          >
            <Link href="https://cardano.ideascale.com/c/idea/122619" isExternal={true}>
              <Button variant="outline" width="full" color="gray.800">
                On-chain events
              </Button>
            </Link>
            <Link href="https://cardano.ideascale.com/c/idea/121719" isExternal={true}>
              <Button variant="outline" width="full" color="gray.800">
                Cross-chain support
              </Button>
            </Link>
          </Stack>
          {!isMobile && (<IconButton aria-label="Close Catalyst Banner" icon={<IoClose />} onClick={onClose} variant="ghost" color="gray.800" />)}
        </Stack>
      </Container>
    </Box>
    )
  }
  