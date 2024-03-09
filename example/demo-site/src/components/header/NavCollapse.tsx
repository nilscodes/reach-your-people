import React from 'react';
import {
  Button, Collapse, Stack, Text, useDisclosure,
} from '@chakra-ui/react';
import PopoverIcon from './PopoverIcon';
import { NavItem } from '@/lib/nav';
import NextLink from '../NextLink';

type NavCollapseProps = {
    label: string;
    subnav: NavItem[];
    closeDrawer: () => void;
};

export default function NavCollapse({ label, subnav, closeDrawer }: NavCollapseProps) {
  const { isOpen, onToggle } = useDisclosure();
  return (
    <>
      <Button justifyContent="space-between" variant="tertiary" size="lg" onClick={onToggle}>
        <Text as="span">{label}</Text>
        <PopoverIcon isOpen={isOpen} />
      </Button>
      <Collapse in={isOpen} animateOpacity>
        <Stack spacing="1" alignItems="stretch" ps="4">
          {subnav.map((item) => (<React.Fragment key={item.path}>
            {item.external && <Button as='a' href={item.path} key={item.path} variant="tertiary" justifyContent="start">
              {item.label}
            </Button>}
            {!item.external && <NextLink href={item.path} key={item.path}>
              <Button variant="tertiary" size="lg" justifyContent="start" onClick={closeDrawer}>
                {item.label}
              </Button>
            </NextLink>}
          </React.Fragment>))}
        </Stack>
      </Collapse>
    </>
  );
};
