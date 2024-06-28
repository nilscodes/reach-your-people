import { Center, Container, Stack } from '@chakra-ui/react'
import Step from '../stepper/Step'
import { discordSteps } from './discordSteps';
import useTranslation from 'next-translate/useTranslation';
import { StepProvider, useStepContext } from '../stepper/StepContext';
import { pushapiSteps } from './pushapiSteps';

type TestNotificationModalProps = {
  testType: string;
  externalAccountId: number;
}

interface StepComponentProps {
  id: number
  title: string
  children: React.ReactNode
  isLastStep: boolean
}

const StepComponent = ({ id, title, isLastStep, children }: StepComponentProps) => {
  const { currentStep } = useStepContext();
  return (
    <Step
      title={title}
      isActive={currentStep === id}
      isCompleted={currentStep > id}
      isLastStep={isLastStep}
    >
      {children}
    </Step>
  );
};

const availableSteps: { [key: string]: { title: string; children: React.ReactNode }[] } = {
  discord: discordSteps,
  pushapi: pushapiSteps,
};

export function TestNotificationModal({ testType, externalAccountId }: TestNotificationModalProps) {
  const steps = availableSteps[testType] ?? []
  const { t } = useTranslation('accounts')

  return (
    <StepProvider initialStep={0} maxStep={steps.length - 1} metadata={{ externalAccountId }}>
      <Container py={{ base: '4', md: '8' }} px="0" w="100%">
          <Stack spacing="0">
            {steps.map((step, id) => (
              <StepComponent
                key={id}
                id={id}
                title={t(step.title)}
                isLastStep={steps.length === id + 1}
              >
                {step.children}
              </StepComponent>
            ))}
          </Stack>
      </Container>
    </StepProvider>
  )
}