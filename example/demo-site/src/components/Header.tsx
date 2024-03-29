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

export default function Header() {
  const { data: session } = useSession();
  const { t } = useTranslation('common')
  const router = useRouter();

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if ((e.key === 'Enter' || e.code === '13') && e.currentTarget.value.trim().length > 0) {
      router.push(`/search?term=${encodeURIComponent(e.currentTarget.value)}`);
    }
  };

  return (<Box borderBottomWidth="1px" bg="bg.surface" position="relative" zIndex="tooltip">
    <Container py="4" maxW="100%">
      <HStack justify="space-between" spacing="8">
        <HStack spacing="10">
          <HStack spacing="3">
            <MobileDrawer />
            <NextLink href='/'><Logo h="48px" w="192px" /></NextLink>
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
              return (<Button key={item.label} onClick={() => {
                if (!item.external) {
                  router.push(item.path);
                }
              }}>{t(item.label)}</Button>)
            })}
          </ButtonGroup>
        </HStack>
        <HStack spacing={{ base: '2', md: '4' }}>
          <InputGroup maxW="2xs" display={{ base: 'none', md: 'inline-flex' }}>
            <InputLeftElement>
              <Icon as={FiSearch} color="fg.accent.muted" fontSize="lg" />
            </InputLeftElement>
            <Input placeholder={t('searchPlaceholder')} variant={mode('outline', 'bg.surface')} onKeyUp={handleKeyPress} />
          </InputGroup>
          <ButtonGroup spacing="1">
            <IconButton
              icon={<FiSearch />}
              aria-label={t('searchPlaceholder')}
              display={{ base: 'flex', lg: 'none' }}
              isRound
            />
            <ColorModeSwitcher isRound />
            <AuthButton />
          </ButtonGroup>
          {session?.user?.image && (<Avatar boxSize="10" src={session?.user?.image} bgColor='white' />)}
        </HStack>
      </HStack>
    </Container>
  </Box>)
};
