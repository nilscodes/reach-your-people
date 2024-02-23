import { chakra } from '@chakra-ui/react';
import NextImage from 'next/image';

export default chakra(NextImage, {
  shouldForwardProp: (prop) => ['height', 'width', 'quality', 'src', 'alt'].includes(prop)
});