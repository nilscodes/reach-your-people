import React, { useEffect, useState } from 'react';
import { Container, Heading, Text, Button, Box, VStack, Stack, useToast } from '@chakra-ui/react';
import WalletSettingsForm from './WalletSettingsForm';
import { Account, GetLinkedExternalAccounts200ResponseInner } from '@/lib/ryp-api';

type WalletSettingsListProps = {
    account: Account;
    wallets: GetLinkedExternalAccounts200ResponseInner[];
};

export default function WalletSettingsList({ account, wallets }: WalletSettingsListProps) {
    return (<Box
      maxW="7xl"
      mx="auto"
      px={{ base: '4', md: '8', lg: '12' }}
      py={{ base: '6', md: '8', lg: '12' }}
    >
      <Stack spacing="8">
        <Heading size={{ base: 'xs', md: 'sm' }}>Your Wallet Settings and Standard Subscriptions</Heading>
        <Stack spacing="3">
          {wallets.map((wallet) => {
              return (<WalletSettingsForm
                key={wallet.externalAccount.id}
                wallet={wallet}
              />);
            })}
            
        </Stack>
      </Stack>
    </Box>);
}