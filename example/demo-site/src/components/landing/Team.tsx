import {
  Avatar,
  Box,
  Button,
  Container,
  Heading,
  HStack,
  Icon,
  Link,
  SimpleGrid,
  Stack,
  Text,
} from '@chakra-ui/react'
import useTranslation from 'next-translate/useTranslation'
import { FaLinkedin, FaTwitter } from 'react-icons/fa'

export const members = [
  {
    role: 'team.founder',
    image: '/nils.jpg',
    name: 'Nils Codes',
    description: 'team.nilsBio',
    socials: [{
      type: 'linkedin',
      icon: FaLinkedin,
      url: 'https://www.linkedin.com/in/nils-peuser-437784a'
    }, {
      type: 'twitter',
      icon: FaTwitter,
      url: 'https://twitter.com/nilscodes'
    }]
  },
  {
    role: 'team.projectManager',
    image: '/nicole.jpg',
    name: 'Nicole',
    description: 'team.nicoleBio',
    socials: []
  },
]

export const Team = () => {
  const { t } = useTranslation('common');
  return (<Box>
    <Container py={{ base: '16', md: '24' }}>
      <Stack spacing={{ base: '12', xl: '24' }} direction={{ base: 'column', lg: 'row' }}>
        <Stack spacing="10">
          <Stack spacing="3" maxW="sm" width="full">
            {/* <Text fontSize={{ base: 'sm', md: 'md' }} color="accent" fontWeight="semibold">
              We're hiring
            </Text> */}
            <Stack spacing={{ base: '4', md: '5' }}>
              <Heading size={{ base: 'sm', md: 'md' }}>{t('team.headline')}</Heading>
              <Text fontSize={{ base: 'lg', md: 'xl' }} color="fg.muted" maxW="3xl">
                {t('team.subtitle')}
              </Text>
            </Stack>
          </Stack>
          <Stack spacing="3" direction={{ base: 'column-reverse', md: 'row' }}>
            <Button as="a" href="https://discord.gg/nzka3K2WUS" target="_blank" variant="secondary" size="xl">
              {t('team.cta')}
            </Button>
            {/* <Button size="xl">Join our team</Button> */}
          </Stack>
        </Stack>
        <SimpleGrid
          columns={{ base: 1, md: 2 }}
          columnGap="8"
          rowGap={{ base: '10', lg: '12' }}
          flex="1"
        >
          {members.map((member) => (
            <Stack key={member.name} spacing={{ base: '4', md: '5' }} direction="row">
              <Avatar src={member.image} boxSize={{ base: '16', md: '20' }} />
              <Stack spacing="4">
                <Stack>
                  <Box>
                    <Text fontWeight="medium" fontSize="lg">
                      {member.name}
                    </Text>
                    <Text color="accent">{t(member.role)}</Text>
                  </Box>
                  <Text color="fg.muted">{t(member.description)}</Text>
                </Stack>
                <HStack spacing="4" color="fg.subtle">
                  {member.socials.map((item) => (
                    <Link href={item.url} key={item.type}>
                      <Icon as={item.icon} boxSize="5" />
                    </Link>
                  ))}
                </HStack>
              </Stack>
            </Stack>
          ))}
        </SimpleGrid>
      </Stack>
    </Container>
  </Box>)
}
