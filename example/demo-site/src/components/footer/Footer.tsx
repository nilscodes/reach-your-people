import { Box, Stack } from '@chakra-ui/react'
import { Copyright } from './Copyright'
import { SocialMediaLinks } from './SocialMediaLinks'
import NextLink from '../NextLink'

interface Link {
  label: string,
  href?: string,
  children?: Array<{
    label: string
    description?: string
    href: string
    icon?: React.ReactElement
  }>
}

const footerlinks: Link[] = [
  { label: 'Privacy Policy', href: '/legal/privacypolicy' },
  { label: 'Terms and Conditions', href: '/legal/terms' },
  { label: 'Support', href: 'https://discord.gg/nzka3K2WUS' },
]

export const Footer = () => (
  <Box as="footer" role="contentinfo" mx="auto" maxW="7xl" py="12" px={{ base: '4', md: '8' }}>
    <Stack direction={{ base: 'column-reverse', md: 'row' }} spacing="4" align="center" justify="space-between">
      {/* <Logo h="3em" w="2.75em" /> */}
      <Copyright alignSelf={{ base: 'center' }} />
      <Stack direction={{ base: 'column-reverse', md: 'row' }} as="ul" id="nav__footer-links" aria-label="Footer Links" listStyleType="none" p="1">
        {footerlinks.map((link, idx) => (
          <Box as="li" key={idx} id={`nav__menuitem-${idx}`} textAlign={{ base: 'center', md: 'left' }}>
            <NextLink href={link.href!} fontSize="sm" p={{ 'base': 1 , 'md': 4 }}>{link.label}</NextLink>
          </Box>
        ))}
      </Stack>
      <SocialMediaLinks />
    </Stack>
  </Box>
)