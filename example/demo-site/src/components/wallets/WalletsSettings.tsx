import {
    Box,
    BoxProps,
    Stack,
    StackDivider,
    Switch,
    Tag,
    Text,
    Wrap,
  } from '@chakra-ui/react'
import NextLink from '../NextLink';
  
  export default function WalletSettings(props: BoxProps) {
    return (<Box as="form" bg="bg.surface" boxShadow="sm" borderRadius="lg" {...props}>
      <Stack spacing="5" px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }} divider={<StackDivider />}>
        <Stack justify="space-between" direction="row" spacing="16">
          <Stack spacing="0.5" fontSize="sm">
            <Text color="fg.emphasized" fontWeight="bold">
              Fungible Tokens and NFTs
            </Text>
            <Text color="fg.muted">By default, subscribe to all communications from the tokens in this wallet. You can unsubscribe from specific tokens under <NextLink href='/subscriptions'>Subscriptions</NextLink>.</Text>
          </Stack>
          <Switch defaultChecked={true} />
        </Stack>
        <Stack justify="space-between" direction="row" spacing="16">
          <Stack spacing="0.5" fontSize="sm">
            <Text color="fg.emphasized" fontWeight="bold">
              Delegated Stake Pools (SPOs)
            </Text>
            <Text color="fg.muted">By default, subscribe to all communications from the Stake Pool Operators this wallet is delegated to. You can unsubscribe from specific pools under <NextLink href='/subscriptions'>Subscriptions</NextLink>.</Text>
            <Wrap spacing="2"><Tag>HAZEL</Tag></Wrap>
          </Stack>
          <Switch defaultChecked={true} />
        </Stack>
        <Stack justify="space-between" direction="row" spacing="16">
          <Stack spacing="0.5" fontSize="sm">
            <Text color="fg.emphasized" fontWeight="bold">
              Delegated Representatives (dReps)
            </Text>
            <Text color="fg.muted">By default, subscribe to all communications from your delegated representatives. You can unsubscribe from specific pools under <NextLink href='/subscriptions'>Subscriptions</NextLink>.</Text>
          </Stack>
          <Switch defaultChecked={true} />
        </Stack>
      </Stack>
    </Box>);
  }
  