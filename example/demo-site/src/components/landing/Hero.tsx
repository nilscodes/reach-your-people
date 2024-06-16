import { Badge, Box, Button, Container, Heading, IconButton, Img, Stack, Text } from '@chakra-ui/react'
import useTranslation from "next-translate/useTranslation";
import { useRouter } from 'next/router';

export default function Hero() {
  const { t } = useTranslation('common')
  const router = useRouter();

  const heroImg = (router.query?.heroImg as string) || '/ryp-hero-1.jpg';

  return (<Box position="relative" height={{ lg: '720px' }}>
    <Container py={{ base: '16', md: '24' }} height="full">
      <Stack
        direction={{ base: 'column', lg: 'row' }}
        spacing={{ base: '16' }}
        align={{ lg: 'center' }}
        height="full"
      >
        <Stack spacing={{ base: '8', md: '12' }}>
          <Stack spacing="4">
            <Badge
              variant="pill"
              colorScheme="blue"
              alignSelf="start"
              size={{ base: 'md', md: 'lg' }}
            >
              {t('hero.betaLaunch')}
            </Badge>
            <Stack spacing={{ base: '4', md: '6' }} maxW={{ md: 'xs', lg: 'md', xl: 'md' }}>
              <Heading size={{ base: 'md', md: 'xl' }}>{t('hero.headline')}</Heading>
              <Text fontSize={{ base: 'lg', md: 'xl' }} color="fg.muted">
                {t('hero.subtitle')}
              </Text>
            </Stack>
          </Stack>
          <Stack direction={{ base: 'column', md: 'row' }} spacing="3">
            <Button as="a" href="/login" size={{ base: 'lg', md: 'xl' }}>{t('hero.cta')}</Button>
            <Button as="a" href="/projects" variant="secondary" size={{ base: 'lg', md: 'xl' }}>
              {t('hero.ctaAlt')}
            </Button>
          </Stack>
        </Stack>
        <Box
          pos={{ lg: 'absolute' }}
          right="0"
          bottom="0"
          w={{ base: 'full', lg: '50%' }}
          height={{ base: '96', lg: 'full' }}
          sx={{
            clipPath: { lg: 'polygon(10% 0%, 100% 0%, 100% 100%, 0% 100%)' },
          }}
        >
          <Img
            style={{filter: 'sepia(0.4)'}}
            boxSize="full"
            objectFit="cover"
            src={heroImg}
            alt={t('hero.imageAlt')}
          />
        </Box>
      </Stack>
    </Container>
  </Box>);
}
