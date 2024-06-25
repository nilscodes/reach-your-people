import { Circle, Flex, Heading, Stack, StackProps, Text, useColorModeValue } from '@chakra-ui/react'
import * as React from 'react'

export interface ListItemProps extends StackProps {
  title: string
  subTitle?: string
  icon?: React.ReactElement
  isLastItem?: boolean
  completed?: boolean
}

export const ListItem = (props: ListItemProps) => {
  const { title, subTitle, icon, isLastItem, completed, children, ...stackProps } = props

  const iconBg = useColorModeValue(completed ? 'green.500' : 'brand.500', completed? 'green.300' : 'brand.300');

  return (
    <Stack as="li" direction="row" spacing="4" {...stackProps}>
      <Flex direction="column" alignItems="center" aria-hidden="true">
        <Circle
          bg={iconBg}
          size="12"
          borderWidth="4px"
          borderColor={useColorModeValue('white', 'gray.800')}
          color={useColorModeValue('white', 'black')}
          mt={2}
        >
          {icon}
        </Circle>
        {!isLastItem && <Flex flex="1" borderRightWidth="1px" mb="-14" />}
      </Flex>
      <Stack spacing="4" pt="1" flex="1">
        {/* <Flex direction="column">
          <Heading lineHeight='2.5rem' fontSize="md" fontWeight="semibold">
            {title}
          </Heading>
          <Text fontSize="sm" color={useColorModeValue('gray.600', 'gray.400')}>
            {subTitle}
          </Text>
        </Flex> */}
        <Flex>{children}</Flex>
      </Stack>
    </Stack>
  )
}