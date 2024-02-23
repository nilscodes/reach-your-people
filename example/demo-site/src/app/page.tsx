import { Container, Heading } from "@chakra-ui/react";

export default async function Home() {
  return (
    <Container py={{ base: '4', md: '8' }}>
      <Heading>Welcome to RYP</Heading>
    </Container>
  );
}
