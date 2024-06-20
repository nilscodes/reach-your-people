import { CreateExternalAccountRequest, GetLinkedExternalAccounts200ResponseInner, GetLinkedExternalAccounts200ResponseInnerSettingsEnum } from '../lib/ryp-subscription-api';
import { Box, HStack, IconButton, Menu, MenuButton, MenuItem, MenuList, Stack, Tag, Text, useToast } from '@chakra-ui/react'
import { Link } from '@chakra-ui/next-js'
import useTranslation from 'next-translate/useTranslation';
import { FiMoreVertical } from 'react-icons/fi';
import { MdLinkOff, MdNotificationsActive } from 'react-icons/md';
import { providerList } from './ProviderIcons';

type LinkedAccountsProps = {
  linkedAccount: GetLinkedExternalAccounts200ResponseInner;
  icon: JSX.Element;
  showUrl: boolean;
  canRemove: boolean;
  onRemove: (externalAccountId: number) => void;
  makeDefaultForNotifications: (externalAccountId: number) => void;
};

const buildUrlForExternalAccount = (externalAccount: CreateExternalAccountRequest) => {
  if (externalAccount.type === 'cardano') {
    return `https://pool.pm/${externalAccount.referenceId}`;
  }
  return '';
}

export const LinkedAccount = ({
  linkedAccount, icon, canRemove, onRemove, makeDefaultForNotifications, showUrl,
}: LinkedAccountsProps) => {
  const { t } = useTranslation('accounts');
  const { t: tc } = useTranslation('common');
  const toast = useToast();
  const providerName = providerList.find((provider) => provider.id === linkedAccount.externalAccount.type)?.id ?? linkedAccount.externalAccount.type;
  const url = showUrl ? buildUrlForExternalAccount(linkedAccount.externalAccount) : '';
  const hasUrl = url.length > 0;
  const tags: string[] = [];
  const canReceiveNotifications = linkedAccount.externalAccount.type !== 'cardano';

  let isNotDefault = false;
  if (canReceiveNotifications && linkedAccount.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications)) {
    tags.push(t('defaultForNotifications'));
  } else {
    isNotDefault = true;
    if (!canReceiveNotifications) {
      tags.push(t('noNotifications'));
    }
  }

  const checkAndRemove = (externalAccountId: number) => {
    if (canRemove && isNotDefault) {
      onRemove(externalAccountId);
    } else {
      if (!isNotDefault) {
        toast({
          title: t('defaultForNotificationsError'),
          status: "error",
          duration: 5000,
          isClosable: true,
          position: "top",
          variant: "solid",
        });
      } else {
        toast({
          title: t('lastUnlinkError'),
          status: "error",
          duration: 5000,
          isClosable: true,
          position: "top",
          variant: "solid",
        });
      }
    }
  }

  return (<Box as="section">
    <Stack
      direction={{ base: 'column', md: 'row' }}
      spacing={{ base: '5', md: '6' }}
      justify="space-between"
    >
      <Stack spacing="1" flexGrow={1}>
        <HStack>
          {icon}
          <Text textStyle="lg" fontWeight="medium">
            {tc(`types.${providerName}`)}
          </Text>
        </HStack>
        <Text textStyle="sm" color="fg.muted">
          {hasUrl && <Link href={url} isExternal>{linkedAccount.externalAccount.displayName}</Link>}
          {!hasUrl && linkedAccount.externalAccount.displayName}
        </Text>
      </Stack>
      <Stack spacing="1" alignItems="center" flexDirection="row">
        {tags.map((tag) => (
          <Tag as="div" key={tag} textStyle="sm" color="fg.muted">
            {tag}
          </Tag>
        ))}
      </Stack>
      <Box display="flex" alignItems="center" flexDirection="row">
        <Menu>
            <MenuButton
              as={IconButton}
              icon={<FiMoreVertical />}
              variant="ghost"
              aria-label="Options"
              color="fg.muted"
            />
            <MenuList>
              {isNotDefault && canReceiveNotifications && (
                <MenuItem
                  onClick={() => makeDefaultForNotifications(linkedAccount.externalAccount.id!)}
                  icon={<MdNotificationsActive size="1.5em" />}
                >
                  {t('makeDefaultForNotifications')}
                </MenuItem>
              )}
              <MenuItem
                onClick={() => checkAndRemove(linkedAccount.externalAccount.id!)}
                icon={<MdLinkOff size="1.5em" />}
              >
                {t('unlink')}
              </MenuItem>
              
            </MenuList>
          </Menu>
      </Box>
    </Stack>
  </Box>)
};