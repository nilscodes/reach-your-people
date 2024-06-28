import { BoxProps, Divider, Stack, Text } from '@chakra-ui/react'
import { StepCircle } from './StepCircle'
import React from 'react'

interface StepProps extends BoxProps {
  title: string
  children: React.ReactNode
  isCompleted: boolean
  isActive: boolean
  isLastStep: boolean
}

export default function Step(props: StepProps) {
  const { isActive, isCompleted, isLastStep, title, children, ...stackProps } = props

  return (
    <Stack spacing="4" direction="row" {...stackProps}>
      <Stack spacing="0" align="center">
        <StepCircle isActive={isActive} isCompleted={isCompleted} />
        <Divider
          orientation="vertical"
          borderWidth="1px"
          borderColor={isCompleted ? 'accent' : isLastStep ? 'transparent' : 'inherit'}
        />
      </Stack>
      <Stack spacing="0.5" pb={isLastStep ? '0' : '8'}>
        <Text color="fg.emphasized" fontWeight="medium">
          {title}
        </Text>
        {(isActive || isCompleted) && (children)}
      </Stack>
    </Stack>
  )
}