import {
  IconButton, ButtonProps, Popover, PopoverTrigger, PopoverContent,
  PopoverHeader, PopoverBody, PopoverCloseButton, Stack, Tooltip,
  Spinner,
  StackDivider,
  Box,
  Checkbox,
  HStack,
  Text,
  Button
} from '@chakra-ui/react';
import { ChangeEvent, forwardRef, useState } from 'react';
import useTranslation from 'next-translate/useTranslation';
import { useApi } from '@/contexts/ApiProvider';
import { GetLinkedExternalAccounts200ResponseInner, GetLinkedExternalAccounts200ResponseInnerSettingsEnum } from '@/lib/ryp-subscription-api';
import { MdOutlineEditNotifications, MdWarning } from 'react-icons/md';
import { providerList } from '../ProviderIcons';
import { sortLinkedExternalAccounts } from '../LinkedAccounts';

interface ProjectNotificationSettingsButtonProps extends ButtonProps {
  projectId: number;
  fullButton?: boolean;
}

type ButtonPopoverTriggerProps = ButtonProps & {
  icon: JSX.Element;
  label: string;
  fullButton?: boolean
};

type NotificationAccount = {
  linkId: number;
  name: string;
  icon: JSX.Element;
};

const ButtonPopoverTrigger = forwardRef(function TriggerRef(props: ButtonPopoverTriggerProps, ref) {
  const { icon, label, fullButton, ...rest } = props;
  if (!fullButton) {
    return (<Tooltip label={label} aria-label={label} hasArrow >
      <IconButton icon={icon} {...rest} aria-label={label} variant='ghost' ref={ref} />
    </Tooltip>)
  } else {
    return (<Button leftIcon={icon} {...rest} aria-label={label} variant='outline' ref={ref}>
      {label}
    </Button>)
  }
});

function findProviderByType(type: string) {
  return providerList.find((provider) => provider.id === type);
}

function prepareNotificationAccounts(linkedAccounts: GetLinkedExternalAccounts200ResponseInner[]) {
  return sortLinkedExternalAccounts(linkedAccounts)
    .filter((linkedAccount) => linkedAccount.externalAccount.type !== 'cardano')
    .reduce<NotificationAccount[]>((arr, linkedAccount) => {
      const provider = findProviderByType(linkedAccount.externalAccount.type);
      if (provider) {
        arr.push({
          linkId: linkedAccount.id,
          name: `types.${provider?.id}`,
          icon: provider?.Component,
        });
      }
      return arr;
    }, []);
}

const NO_DEFAULT_PROVIDER = {
  linkId: 0,
  name: 'types.none',
  icon: <MdWarning />
};

export default function ProjectNotificationSettingsButton({ projectId, fullButton }: ProjectNotificationSettingsButtonProps) {
  const [notificationAccounts, setNotificationAccounts] = useState<NotificationAccount[]>([]);
  const [defaultNotificationAccount, setDefaultNotificationAccount] = useState<NotificationAccount>(NO_DEFAULT_PROVIDER);
  const [currentCheckboxValues, setCurrentCheckboxValues] = useState<string[]>([]);
  const { t } = useTranslation('projects');
  const { t: tc } = useTranslation('common');
  const api = useApi();

  const fetchNotificationSettings = async () => {
    const results = await Promise.all([
      api.getLinkedExternalAccounts(),
      api.getNotificationsSettingsForAccountAndProject(projectId),
    ]);
    let initialCheckboxValues = ['default']
    if (results[1].length > 0) {
      initialCheckboxValues = results[1].map((notificationSetting) => `${notificationSetting.externalAccountLinkId}`);
    }
    setCurrentCheckboxValues(initialCheckboxValues);
    setNotificationAccounts(prepareNotificationAccounts(results[0]));
    setDefaultNotificationAccount(getDefaultNotificationAccountFromLinkedAccounts(results[0]));
  };

  function getDefaultNotificationAccountFromLinkedAccounts(linkedAccounts: GetLinkedExternalAccounts200ResponseInner[]) {
    const defaultLinkedAccount = linkedAccounts.find((linkedAccount) => linkedAccount.settings?.includes(GetLinkedExternalAccounts200ResponseInnerSettingsEnum.DefaultForNotifications)) ?? null;
    if (defaultLinkedAccount !== null) {
      const defaultProvider = findProviderByType(defaultLinkedAccount.externalAccount.type);
      if (defaultProvider) {
        return {
          linkId: 0,
          name: `types.${defaultProvider.id}`,
          icon: defaultProvider.Component,
        };
      }
    }
    return NO_DEFAULT_PROVIDER;
  }

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    const changedValue = event.target.value;
    if (changedValue === 'default') {
      setCurrentCheckboxValues(['default']);
      api.updateNotificationsSettingsForAccountAndProject(projectId, []);
    } else {
      const newNotificationSettings = [...currentCheckboxValues.filter((item) => item !== 'default' && item !== changedValue)];
      if (event.target.checked) {
        newNotificationSettings.push(changedValue)
      }
      if (newNotificationSettings.length === 0) {
        newNotificationSettings.push('default');
      }
      setCurrentCheckboxValues(newNotificationSettings);
      api.updateNotificationsSettingsForAccountAndProject(projectId, newNotificationSettings.map((item) => ({
        projectId,
        externalAccountLinkId: parseInt(item)
      })));
    }
  }

  return (
    <Popover onOpen={fetchNotificationSettings}>
      <PopoverTrigger>
        <ButtonPopoverTrigger icon={<MdOutlineEditNotifications />} label={t('projectNotificationSettings.buttonText')} fullButton={fullButton} />
      </PopoverTrigger>
      <PopoverContent>
        <PopoverHeader fontWeight="semibold" fontSize="lg" mt="1">{t('projectNotificationSettings.label')}</PopoverHeader>
        <PopoverCloseButton size='xs' />
        <PopoverBody>
          {notificationAccounts.length === 0 && (<Spinner />)}
          {notificationAccounts.length > 0 && (
            <Stack spacing="2" divider={<StackDivider />} my="2">
              <Box>
                <Checkbox
                  value="default"
                  isChecked={currentCheckboxValues.includes('default')}
                  onChange={handleChange}
                >
                  <HStack>
                    {defaultNotificationAccount.icon}
                    <Text>
                      {t('projectNotificationSettings.default', { defaultNotificationAccount: tc(defaultNotificationAccount.name) })}
                    </Text>
                  </HStack>
                </Checkbox>
              </Box>
              <Stack spacing="2">
                {notificationAccounts.map((linkedAccount) => {
                  return (
                    <Checkbox
                      key={linkedAccount.linkId}
                      value={linkedAccount.linkId}
                      isChecked={currentCheckboxValues.includes(`${linkedAccount.linkId}`)}
                      onChange={handleChange}
                    >
                      <HStack>
                        {linkedAccount.icon}
                        <Text>
                          {tc(linkedAccount.name)}
                        </Text>
                      </HStack>
                    </Checkbox>
                  );
                })}
              </Stack>
            </Stack>
          )}
        </PopoverBody>
      </PopoverContent>
    </Popover>
  );
};
