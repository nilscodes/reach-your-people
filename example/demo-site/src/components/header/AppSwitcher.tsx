import {
  Button, HStack, IconButton, Popover, PopoverContent, PopoverTrigger, Stack, Text, useDisclosure,
} from '@chakra-ui/react';
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';
import { Logo, VibrantLogo } from '../Logo';
import { IoApps } from 'react-icons/io5';

const apps = [{
  logo: <VibrantLogo h="36px" w="144px" />,
  url: 'https://vibrantnet.io',
  name: 'apps.vibrantNet',
  description: 'apps.vibrantNetDescription'
}, {
  logo: <Logo h="36px" w="90px" />,
  url: 'https://ryp.io',
  name: 'apps.ryp',
  description: 'apps.rypDescription'
}]

export default function AppSwitcher() {
  const { isOpen, onToggle, onClose } = useDisclosure();
  const { t } = useTranslation('common');
  return (
    <Popover isOpen={isOpen} onClose={onClose} openDelay={0} placement='bottom-start'>
      <PopoverTrigger>
        <IconButton
          variant="ghost"
          color="fg.muted"
          size="md"
          icon={<IoApps size="1.5em" />}
          display={{ base: 'none', lg: 'flex' }}
          transform={isOpen ? 'rotate(-90deg)' : 'rotate(0deg)'}
          transition="transform 0.2s"
          aria-label="Open menu"
          onClick={onToggle}
        />
      </PopoverTrigger>
      <PopoverContent p="2" w="600px">
        <Stack spacing="1">
          {apps.map((item) => (
            <NextLink href={item.url} key={item.url}>
              <HStack spacing="2">
                <Button variant="tertiary" justifyContent="start" aria-label={t(item.name)} w="180px">
                  {item.logo}
                </Button>
                <Text color="fg.muted">{t(item.description)}</Text>
              </HStack>
            </NextLink>
          ))}
        </Stack>
      </PopoverContent>
    </Popover>
  );
};
