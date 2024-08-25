import React, { useState } from 'react';
import { Container, Box, Stack, Tabs, TabList, Tab, TabIndicator, TabPanels, TabPanel, useBreakpointValue } from '@chakra-ui/react';
import { Account, GetLinkedExternalAccounts200ResponseInner } from '../lib/ryp-subscription-api';
import useTranslation from 'next-translate/useTranslation';
import LinkedAccounts from './LinkedAccounts';
import WalletSettingsList from './notifications/WalletSettingsList';
import Achievements from './account/Achievements';
import { Achievement } from '@/lib/types/Achievement';
import PremiumAccount from './account/PremiumAccount';
import NotificationSettings from './notifications/NotificationSettings';


type AccountsPageProps = {
  account: Account;
  linkedAccounts: GetLinkedExternalAccounts200ResponseInner[];
  accountSettings: Record<string, string>;
  currentTab: string;
  achievements?: Achievement[];
};

const tabs = ['accounts', 'notifications', 'achievements', 'premium'];
const tabUrls = ['/account', '/account/notifications', '/account/achievements', '/account/premium'];

export default function AccountsPage({ account, accountSettings, linkedAccounts, achievements, currentTab }: AccountsPageProps) {
  const [tabIndex, setTabIndex] = useState(tabs.indexOf(currentTab));
  const tabOrientation = useBreakpointValue({ base: 'horizontal', md: 'vertical' }) as "vertical" | "horizontal";
  const { t } = useTranslation('accounts');

  const changeTab = (index: number) => {
    setTabIndex(index);
    window.history.pushState({}, '', tabUrls[index]);
    // Using router.replace shows weird React artifacts router.replace(tabUrls[index], undefined, { shallow: true });
  }

  return (<Tabs size='lg' variant="underline" orientation={tabOrientation} defaultIndex={tabIndex} onChange={(index) => changeTab(index)}>
    <Container maxW="5xl">
      <Stack direction={{ base: 'column', md: 'row' }}>
        <Box pt={{ base: '12', md: '24' }} pb={{ base: '0', md: '24' }}>
          <TabList>
            {tabs.map((tab) => (
              <Tab key={tab}>
                {t(`tabs.${tab}`)}
              </Tab>
            ))}
          </TabList>
          <TabIndicator />
        </Box>
        <TabPanels>
          <TabPanel>
            <LinkedAccounts account={account} linkedAccounts={linkedAccounts} accountSettings={accountSettings} />
          </TabPanel>
          <TabPanel>
            <NotificationSettings account={account} wallets={linkedAccounts.filter((account) => account.externalAccount.type === 'cardano')} />
          </TabPanel>
          <TabPanel>
            <Achievements account={account} achievements={achievements ?? []} />
          </TabPanel>
          <TabPanel>
            <PremiumAccount account={account} />
          </TabPanel>
        </TabPanels>
      </Stack>
    </Container>
  </Tabs>);

};
