import { CreateExternalAccountRequest, GetLinkedExternalAccounts200ResponseInner } from '../lib/ryp-subscription-api';
import { Box, Button, Container, HStack, Stack, Text, useToast } from '@chakra-ui/react'
import { Link } from '@chakra-ui/next-js'
import useTranslation from 'next-translate/useTranslation';

type LinkedAccountsProps = {
  linkedAccount: GetLinkedExternalAccounts200ResponseInner;
  icon: JSX.Element;
  showUrl: boolean;
  canRemove: boolean;
  onRemove: (externalAccountId: number) => void;
};

const buildUrlForExternalAccount = (externalAccount: CreateExternalAccountRequest) => {
  if (externalAccount.type === 'cardano') {
    return `https://pool.pm/${externalAccount.referenceId}`;
  }
  return '';
}

export const LinkedAccount = ({
  linkedAccount, icon, canRemove, onRemove, showUrl,
}: LinkedAccountsProps) => {
  const { t } = useTranslation('accounts');
  const toast = useToast();
  // TODO Provider name is external account type with the first letter capitalized for now
  const providerName = linkedAccount.externalAccount.type.charAt(0).toUpperCase() + linkedAccount.externalAccount.type.slice(1);
  const url = showUrl ? buildUrlForExternalAccount(linkedAccount.externalAccount) : '';
  const hasUrl = url.length > 0;
  return (<Box as="section">
    <Container maxW="3xl">
      <Box bg="bg.surface" boxShadow="sm" borderRadius="lg" p={{ base: '4', md: '6' }}>
        <Stack
          direction={{ base: 'column', md: 'row' }}
          spacing={{ base: '5', md: '6' }}
          justify="space-between"
        >
          <Stack spacing="1">
            <HStack>
              {icon}
              <Text textStyle="lg" fontWeight="medium">
                {providerName}
              </Text>
            </HStack>
            <Text textStyle="sm" color="fg.muted">
              {hasUrl && <Link href={url} isExternal>{linkedAccount.externalAccount.displayName}</Link>}
              {!hasUrl && linkedAccount.externalAccount.displayName}
            </Text>
          </Stack>
          <Box>
            <Button onClick={() => {
              if (canRemove) {
                onRemove(linkedAccount.externalAccount.id!);
              } else {
                toast({
                  title: 'You must have at least one linked social account and cannot remove the last wallet if you have only wallets connected.',
                  status: "error",
                  duration: 5000,
                  isClosable: true,
                  position: "top",
                  variant: "solid",
                });
              }
            }}>{t('unlink')}</Button>
          </Box>
        </Stack>
      </Box>
    </Container>
  </Box>)
};