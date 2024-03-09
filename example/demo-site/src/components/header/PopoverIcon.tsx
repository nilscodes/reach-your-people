import { Icon } from '@chakra-ui/react';
import { FiChevronDown } from 'react-icons/fi';

export default function PopoverIcon(props: { isOpen: boolean }) {
  const iconStyles = {
    transform: props.isOpen ? 'rotate(-180deg)' : undefined,
    transition: 'transform 0.2s',
    transformOrigin: 'center',
  };
  return <Icon aria-hidden as={FiChevronDown} __css={iconStyles} />;
};
