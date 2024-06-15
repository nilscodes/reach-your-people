import { Container, Heading, Text, Box, Stack, StackDivider, ContainerProps } from '@chakra-ui/react';

interface CardProps extends ContainerProps {
  heading?: string;
  description?: string;
  children?: React.ReactNode;
}

export default function Card({ heading, description, children, ...rest }: CardProps) {
  return (<Box as="section" pt={{ base: '4', md: '8' }}>
    <Container px="0" {...rest}>
      <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
        {(heading || description) && (
          <Stack spacing="1" pb="5">
            {heading && (
              <Heading size="xs">
                {heading}
              </Heading>
            )}
            {description && (
              <Text textStyle="sm" color="fg.muted">
                {description}
              </Text>
            )}
        </Stack>)}
        {children}
      </Box>
    </Container>
  </Box>);
};
