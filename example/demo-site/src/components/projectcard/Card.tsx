import { Box, BoxProps, useColorModeValue } from '@chakra-ui/react';

export default function Card(props: BoxProps) {
  return (
  <Box
    bg={useColorModeValue('gray.50', 'gray.700')}
    maxWidth="2xl"
    mx="auto"
    p={{ base: '6', md: '8' }}
    rounded={{ sm: 'lg' }}
    shadow={{ md: 'base' }}
    {...props}
    h="100%"
  />);
}