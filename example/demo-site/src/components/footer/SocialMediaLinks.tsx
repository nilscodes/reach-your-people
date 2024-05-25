import { ButtonGroup, ButtonGroupProps, IconButton } from '@chakra-ui/react'
import { FaGithub, FaLinkedin, FaTwitter, FaDiscord } from 'react-icons/fa'

export const SocialMediaLinks = (props: ButtonGroupProps) => (
  <ButtonGroup variant="ghost" color="gray.600" {...props}>
    <IconButton as="a" href="https://discord.gg/nzka3K2WUS" aria-label="LinkedIn" icon={<FaDiscord fontSize="20px" />} />
    <IconButton as="a" href="https://www.linkedin.com/in/nils-peuser-437784a/" aria-label="LinkedIn" icon={<FaLinkedin fontSize="20px" />} />
    <IconButton as="a" href="https://github.com/nilscodes/hazelnet" aria-label="GitHub" icon={<FaGithub fontSize="20px" />} />
    <IconButton as="a" href="https://twitter.com/VibrantNet_io" aria-label="Twitter" icon={<FaTwitter fontSize="20px" />} />
  </ButtonGroup>
)