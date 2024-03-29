import { Container, Heading } from "@chakra-ui/react";
import useTranslation from "next-translate/useTranslation";
import Head from "next/head";

export default function Home() {
  const { t } = useTranslation('common')
  return (<>
    <Head>
      <title>RYP</title>
    </Head>
    <Container py={{ base: '4', md: '8' }}>
      <Heading>{t('welcome')}</Heading>
    </Container>
  </>);
}
