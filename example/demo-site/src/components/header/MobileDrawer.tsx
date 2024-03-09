import {
  Button, Drawer, DrawerBody, DrawerContent, Stack, useDisclosure,
} from '@chakra-ui/react';
import ToggleButton from './ToggleButton';
import nav from '@/lib/nav';
import NavCollapse from './NavCollapse';
import NextLink from '../NextLink';

export default function MobileDrawer() {
  const { isOpen, onToggle, onClose } = useDisclosure();
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
              {nav.map((item) => {
                if (item.children?.length) {
                  return (
                    <NavCollapse
                      key={item.label}
                      label={item.label}
                      subnav={item.children}
                      closeDrawer={onClose}
                    />
                  );
                }
                return (
                  <NextLink href={item.path} key={item.label}>
                    <Button variant="tertiary" justifyContent="start">
                      {item.label}
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
