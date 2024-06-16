import {
  Box,
  Container,
  Heading,
  Icon,
  SimpleGrid,
  Square,
  Stack,
  Text,
} from '@chakra-ui/react'
import { BsStars } from 'react-icons/bs'
import { FaBullhorn, FaCheck, FaVolumeMute } from 'react-icons/fa'
import { IoPeople } from "react-icons/io5";
import useTranslation from 'next-translate/useTranslation'
import { MdHandyman, MdSecurity } from 'react-icons/md'

export const features = [
  {
    name: 'features.subscriptionManagement',
    description: 'features.subscriptionManagementDescription',
    icon: BsStars,
  },
  {
    name: 'features.socialConnectors',
    description: 'features.socialConnectorsDescription',
    icon: IoPeople,
  },
  {
    name: 'features.mute',
    description: 'features.muteDescription',
    icon: FaVolumeMute,
  },
  {
    name: 'features.customization',
    description: 'features.customizationDescription',
    icon: MdHandyman,
  },
  {
    name: 'features.publishing',
    description: 'features.publishingDescription',
    icon: FaBullhorn,
  },
  {
    name: 'features.security',
    description: 'features.securityDescription',
    icon: MdSecurity,
  },
]

export const Features = () => {
  const { t } = useTranslation('common')

  return (<Box as="section" bg="bg.surface">
    <Container py={{ base: '16', md: '24' }}>
      <Stack spacing={{ base: '12', md: '16' }}>
        <Stack spacing={{ base: '4', md: '5' }} maxW="3xl">
          <Stack spacing="3">
            <Heading size={{ base: 'sm', md: 'md' }}>
              {t('features.headline')}
            </Heading>
          </Stack>
          <Text color="fg.muted" fontSize={{ base: 'lg', md: 'xl' }}>
            {t('features.subtitle')}
          </Text>
        </Stack>
        <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} columnGap={8} rowGap={{ base: 10, md: 16 }}>
          {features.map((feature) => (
            <Stack key={feature.name} spacing={{ base: '4', md: '5' }}>
              <Square
                size={{ base: '10', md: '12' }}
                bg="accent"
                color="fg.inverted"
                borderRadius="lg"
              >
                <Icon as={feature.icon} boxSize={{ base: '5', md: '6' }} />
              </Square>
              <Stack spacing={{ base: '1', md: '2' }} flex="1">
                <Text fontSize={{ base: 'lg', md: 'xl' }} fontWeight="medium">
                  {t(feature.name)}
                </Text>
                <Text color="fg.muted">{t(feature.description)}</Text>
              </Stack>
              {/* <Button
                variant="text"
                colorScheme="blue"
                rightIcon={<FiArrowRight />}
                alignSelf="start"
              >
                Read more
              </Button> */}
            </Stack>
          ))}
        </SimpleGrid>
      </Stack>
    </Container>
  </Box>);
}