import { Box, Container, Stack, StackDivider, Text } from '@chakra-ui/react'
import { LinkExternalAccount200Response } from '../../lib/ryp-subscription-api';
import WalletSettings from './WalletsSettings';

export default function WalletSettingsForm({ wallet }: { wallet: LinkExternalAccount200Response }) {
    return (<Container py={{ base: '4', md: '8' }}>
        <Stack spacing="5" divider={<StackDivider />}>
            <Stack
                direction={{ base: 'column', lg: 'row' }}
                spacing={{ base: '5', lg: '8' }}
                justify="space-between"
            >
                <Box flexShrink={0}>
                    <Text textStyle="lg" fontWeight="medium">
                        {wallet.externalAccount.displayName}
                    </Text>
                    <Text color="fg.muted" textStyle="sm">
                        Your settings for this wallet
                    </Text>
                </Box>
                <WalletSettings maxW={{ base: '5xl', lg: '3xl' }} />
            </Stack>
        </Stack>
    </Container>);
}