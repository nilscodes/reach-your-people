import { Features } from '@/components/landing/Features';
import Hero from '@/components/landing/Hero';
import { Team } from '@/components/landing/Team';
import { SpacebudzTimer } from '@/components/timer/SpacebudzTimer';
import { Img } from '@chakra-ui/react'

import useTranslation from "next-translate/useTranslation";
import Head from "next/head";

export default function Home() {
  const { t } = useTranslation('common')
  return (<>
    <Head>
      <title>RYP</title>
    </Head>
    <SpacebudzTimer />
    <Hero />
    <Features />
    <Team />
  </>);
}
