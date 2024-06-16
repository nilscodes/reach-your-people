import React from 'react';
import {
  Button,
  Popover,
  PopoverContent,
  PopoverTrigger,
  Stack,
  useDisclosure,
} from '@chakra-ui/react';
import PopoverIcon from './PopoverIcon';
import { NavItem } from '@/lib/nav';
import NextLink from '../NextLink';

type NavPopoverProps = {
    label: string;
    subnav: NavItem[];
};

export default function NavPopover({ label, subnav }: NavPopoverProps) {
  const { isOpen, onOpen, onClose } = useDisclosure();
  return (
    <Popover isOpen={isOpen} onOpen={onOpen} onClose={onClose} trigger="hover" openDelay={0} placement='bottom-start'>
      <PopoverTrigger>
        <Button rightIcon={<PopoverIcon isOpen={isOpen} />} variant="ghost" borderRadius="25" color="fg.muted">{label}</Button>
      </PopoverTrigger>
      <PopoverContent p="2" maxW="fit-content" borderRadius="25">
        <Stack spacing="0" alignItems="stretch">
          {subnav.map((item) => (<React.Fragment key={item.path}>
            {item.external && <Button as='a' href={item.path} key={item.path} variant="tertiary" justifyContent="start">
              {item.label}
            </Button>}
            {!item.external && <NextLink href={item.path} onClick={() => onClose()}>
              <Button variant="ghost" color="fg.muted" borderRadius="25" justifyContent="start">
                {item.label}
              </Button>
            </NextLink>}
          </React.Fragment>))}
        </Stack>
      </PopoverContent>
    </Popover>
  );
};
