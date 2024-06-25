import React, { useEffect, useMemo } from 'react';
import { Button, Stack, Box, useColorModeValue as mode, Text, Heading, Flex } from '@chakra-ui/react';
import { Account, GetLinkedExternalAccounts200ResponseInner } from '../../lib/ryp-subscription-api';
import { useApi } from '@/contexts/ApiProvider';
import { MdCheckCircle, MdCheckCircleOutline, MdClose, MdHourglassTop } from 'react-icons/md';
import useTranslation from 'next-translate/useTranslation';
import Card from '../Card';
import { List } from '../ui/List';
import { ListItem } from '../ui/ListItem';
import { FirstStepsItems, bitmaskToEnum } from '@/lib/types/FirstSteps';

type FirstStepsProps = {
  account: Account;
  linkedAccounts: GetLinkedExternalAccounts200ResponseInner[];
  accountSettings: Record<string, string>;
  onFinishSteps: (firstSteps: FirstStepsItems[]) => void;
};

function Step1() {
  const { t } = useTranslation('accounts');
  return (
    <Text>{t('firstSteps.step1.description')}</Text>
  );
}

function Step2() {
  const { t } = useTranslation('accounts');
  return (
    <Text>{t('firstSteps.step2.description')}</Text>
  );
}

function Step3() {
  const { t } = useTranslation('accounts');
  return (
    <Text>{t('firstSteps.step3.description')}</Text>
  );
}

function Step4() {
  const { t } = useTranslation('accounts');
  return (
    <Text>{t('firstSteps.step4.description')}</Text>
  );
}


export default function FirstSteps({ account, accountSettings, linkedAccounts, onFinishSteps }: FirstStepsProps) {
  const api = useApi()
  const { t } = useTranslation('accounts');
  const [completeButtonText, setCompleteButtonText] = React.useState('firstSteps.doNotShowAgain');
  const [completeButtonIcon, setCompleteButtonIcon] = React.useState(<MdClose />);
  const [firstStepsCompleted, setFirstStepsCompleted] = React.useState(bitmaskToEnum(+accountSettings['FIRST_STEPS'] ?? 0));

  const steps = useMemo(() => {
    if (firstStepsCompleted.includes(FirstStepsItems.ConnectNotification)
      && firstStepsCompleted.includes(FirstStepsItems.ConnectWallet)
      && firstStepsCompleted.includes(FirstStepsItems.SubscribeExplicitly)
      && firstStepsCompleted.includes(FirstStepsItems.ReferFriend)) {
      setCompleteButtonText('firstSteps.complete');
      setCompleteButtonIcon(<MdCheckCircle />);
    }
    return [
      {
        title: t('firstSteps.step1.title'),
        icon: firstStepsCompleted.includes(FirstStepsItems.ConnectNotification) ? <MdCheckCircleOutline size="1.5em" /> : <MdHourglassTop size="1.5em" />,
        log: <Step1 />,
        completed: firstStepsCompleted.includes(FirstStepsItems.ConnectNotification),
      },
      {
        title: t('firstSteps.step2.title'),
        icon: firstStepsCompleted.includes(FirstStepsItems.ConnectWallet) ? <MdCheckCircleOutline size="1.5em" /> : <MdHourglassTop size="1.5em" />,
        log: <Step2 />,
        completed: firstStepsCompleted.includes(FirstStepsItems.ConnectWallet),
      },
      {
        title: t('firstSteps.step3.title'),
        icon: firstStepsCompleted.includes(FirstStepsItems.SubscribeExplicitly) ? <MdCheckCircleOutline size="1.5em" /> : <MdHourglassTop size="1.5em" />,
        log: <Step3 />,
        completed: firstStepsCompleted.includes(FirstStepsItems.SubscribeExplicitly),
      },
      {
        title: t('firstSteps.step4.title'),
        icon: firstStepsCompleted.includes(FirstStepsItems.ReferFriend) ? <MdCheckCircleOutline size="1.5em" /> : <MdHourglassTop size="1.5em" />,
        log: <Step4 />,
        completed: firstStepsCompleted.includes(FirstStepsItems.ReferFriend),
      },
    ];
  }, [firstStepsCompleted, t]);

  const endFirstSteps = async () => {
    const finishedSteps = await api.endFirstSteps();
    onFinishSteps(bitmaskToEnum(+finishedSteps.value));
  };

  useEffect(() => {
    const updateFirstSteps = async () => {
      const firstStepsSetting = await api.updateFirstSteps();
      setFirstStepsCompleted(bitmaskToEnum(+firstStepsSetting.value));
    }
    updateFirstSteps();
  }, [api]);

  return (
    <Card heading={t('firstSteps.title')} description={t('firstSteps.description')}>
      <Stack direction={{ base: 'column', sm: 'row' }} spacing="3">

        <List spacing="6">
          {steps.map((step, idx) => (
            <ListItem
              key={`changelog-${step}-${idx}}`}
              title={step.title}
              icon={step.icon}
              completed={step.completed}
            >
              {step.log && (<Box
                bg={mode('gray.50', 'gray.700')}
                width="full"
                rounded="xl"
                p={4}
              >
                <Stack>
                  <Heading lineHeight='2.5rem' fontSize="md" fontWeight="semibold">
                    {step.title}
                  </Heading>
                  {step.log}
                </Stack>
              </Box>
              )}

            </ListItem>
          ))}
        </List>
      </Stack>
      <Flex justify="flex-end" mt={{ base: 4, md: 8 }}>
        <Button
          leftIcon={completeButtonIcon}
          colorScheme="brand"
          onClick={endFirstSteps}
          variant="outline"
        >
          {t(completeButtonText)}
        </Button>
      </Flex>
    </Card>);
};
