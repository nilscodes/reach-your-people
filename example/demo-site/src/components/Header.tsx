import {
  Avatar,
  Box,
  Button,
  ButtonGroup,
  Container,
  HStack,
  Icon,
  IconButton,
  Input,
  InputGroup,
  InputLeftElement,
  InputRightElement,
  useColorModeValue as mode,
} from '@chakra-ui/react';
import { useSession } from "next-auth/react";
import { Logo } from '@/components/Logo'
import AuthButton from './AuthButton';
import NextLink from './NextLink';
import { ColorModeSwitcher } from './ColorModeSwitcher';
import nav from '@/lib/nav';
import { FiSearch } from 'react-icons/fi';
import { useRouter } from 'next/navigation';
import MobileDrawer from './header/MobileDrawer';
import NavPopover from './header/NavPopover';
import useTranslation from 'next-translate/useTranslation'
import { ProfileMenu } from './header/ProfileMenu';
import AppSwitcher from './header/AppSwitcher';
import { useRef, useState } from 'react';
import { MdClear } from 'react-icons/md';

export default function Header() {
  const { data: session } = useSession();
  const { t } = useTranslation('common')
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>(null);
  const [searchTerm, setSearchTerm] = useState('');

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if ((e.key === 'Enter' || e.code === '13') && searchTerm.trim().length > 0) {
      router.push(`/projects/all/?search=${encodeURIComponent(e.currentTarget.value)}`);
      handleClearSearch();
    } else {
      setSearchTerm(e.currentTarget.value);
    }
  };

  const handleClearSearch = () => {
    setSearchTerm('');
    if (inputRef.current) {
      inputRef.current.value = '';
    }
  };

  return (<Box borderBottomWidth="1px" bg="bg.surface" position="relative" zIndex="tooltip">
    <Container py="4" maxW="100%">
      <HStack justify="space-between" spacing="8">
        <HStack spacing="10">
          <HStack spacing="3">
            <MobileDrawer />
            <AppSwitcher />
            <NextLink href='/'><Logo h="48px" w="120px" /></NextLink>
          </HStack>
          <ButtonGroup
            size="lg"
            variant="text.surface"
            spacing="8"
            display={{ base: 'none', lg: 'flex' }}
          >
            {nav.filter((item) => !item.onlyLoggedIn || session?.userId).map((item) => {
              if (item.children?.length) {
                return <NavPopover key={item.label} label={t(item.label)} subnav={item.children} />
              }
              return (<NextLink key={item.label} href={item.path}>
                <Button color="fg.muted" variant="ghost" borderRadius="25">{t(item.label)}</Button>
              </NextLink>)
            })}
          </ButtonGroup>
        </HStack>
        <HStack spacing={{ base: '2', md: '4' }}>
          <InputGroup w="2xs" display={{ base: 'none', md: 'inline-flex' }}>
            <InputLeftElement>
              <Icon as={FiSearch} color="fg.accent.muted" fontSize="lg" />
            </InputLeftElement>
            <Input placeholder={t('searchPlaceholder')} variant={mode('outline', 'bg.surface')} onKeyUp={handleKeyPress} ref={inputRef} />
            {searchTerm?.length && (<InputRightElement>
                <IconButton as={MdClear} color='fg.accent.muted' size="xs" variant="ghost" onClick={handleClearSearch} aria-label={t('clearSearch')} />
              </InputRightElement>)}
          </InputGroup>
          <ButtonGroup spacing="3">
            <IconButton
              icon={<FiSearch />}
              aria-label={t('searchPlaceholder')}
              display={{ base: 'flex', lg: 'none' }}
              isRound
            />
            <ColorModeSwitcher isRound display={{ base: 'none', md: 'inherit' }} />
            {!session && (<AuthButton />)}
          </ButtonGroup>
          {session?.user && (<ProfileMenu user={session.user} />)}
        </HStack>
      </HStack>
    </Container>
  </Box>)
};
