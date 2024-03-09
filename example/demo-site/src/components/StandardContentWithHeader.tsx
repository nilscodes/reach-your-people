import { Box, Container, ContainerProps } from '@chakra-ui/react';

interface StandardContentWithHeaderProps extends ContainerProps {
  header: React.ReactNode;
  children: React.ReactNode;
}

export default function StandardContentWithHeader(props: StandardContentWithHeaderProps) {
  const { header, children, ...containerProps } = props;
  return (<Box as="section" py={{ base: '4', md: '8' }}>
    {header}
    <Container {...containerProps}>
      {children}
    </Container>
  </Box>);
};
