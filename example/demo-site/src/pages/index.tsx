import { Box, Container, useColorModeValue as mode } from '@chakra-ui/react';
import { Features } from '@/components/landing/Features';
import Hero from '@/components/landing/Hero';
import { Team } from '@/components/landing/Team';
import { SpacebudzTimer } from '@/components/timer/SpacebudzTimer';
import Image from '@/components/Image';
import rypFull from '../../public/ryp_full.png';
import rypFullDark from '../../public/ryp_full_dark.png';

import Head from "next/head";
import HowItWorks from '@/components/landing/HowItWorks';

export default function Home() {
  return (<>
    <Head>
      <title>RYP</title>
    </Head>
    <SpacebudzTimer />
    <Hero />
    <HowItWorks />
    <Features />
    <Team />
    <Box as="section" bg="bg.surface">
      <Container py={{ base: '16', md: '24' }}>
        <Image src={mode(rypFull, rypFullDark)} alt="Reach Your People Logo" mx="auto" />
      </Container>
    </Box>
  </>);
}
