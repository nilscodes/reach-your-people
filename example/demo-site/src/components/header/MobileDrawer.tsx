import {
  Button, Drawer, DrawerBody, DrawerContent, Stack, useDisclosure,
} from '@chakra-ui/react';
import ToggleButton from './ToggleButton';
import nav from '@/lib/nav';
import NavCollapse from './NavCollapse';
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';
import { useSession } from 'next-auth/react';

const mobileNav = nav.concat([{
  label: 'nav.apps',
  path: '',
  children: [{
    label: 'apps.vibrantNet',
    external: true,
    path: 'https://vibrantnet.io',
  }],
}])

export default function MobileDrawer() {
  const { data: session } = useSession();
  const { isOpen, onToggle, onClose } = useDisclosure();
  const { t } = useTranslation('common');
  return (
    <>
      <ToggleButton
        isOpen={isOpen}
        onClick={onToggle}
        aria-label="Open menu"
        display={{ base: 'inline-flex', lg: 'none' }}
      />
      <Drawer placement="top" isOpen={isOpen} onClose={onClose}>
        <DrawerContent>
          <DrawerBody mt="72px" p="4">
            <Stack spacing="1">
              {mobileNav.filter((item) => !item.onlyLoggedIn || session?.userId).map((item) => {
                if (item.children?.length) {
                  return (
                    <NavCollapse
                      key={item.label}
                      label={t(item.label)}
                      subnav={item.children}
                      closeDrawer={onClose}
                    />
                  );
                }
                return (
                  <NextLink href={item.path} key={item.label} onClick={() => onClose()}>
                    <Button variant="tertiary" justifyContent="start">
                      {t(item.label)}
                    </Button>
                  </NextLink>
                );
              })}
            </Stack>
          </DrawerBody>
        </DrawerContent>
      </Drawer>
    </>
  );
};
