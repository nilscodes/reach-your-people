import {
  Heading,
  ListItem,
  OrderedList,
  Text,
  UnorderedList,
} from '@chakra-ui/react';

export const components = {
  h1: (props: any) => <Heading as="h1" size="lg" {...props} />,
  h2: (props: any) => <Heading as="h2" size="md" {...props} />,
  h3: (props: any) => <Heading as="h3" size="sm" {...props} />,
  p: (props: any) => <Text {...props} py={2} />,
  ol: (props: any) => <OrderedList {...props} />,
  ul: (props: any) => <UnorderedList {...props} />,
  li: (props: any) => <ListItem {...props} />,
};