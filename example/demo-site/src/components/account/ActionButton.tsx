import { Button, ButtonProps } from '@chakra-ui/react'
import * as React from 'react'

export default function ActionButton(props: ButtonProps) {
  return (<Button
    colorScheme="brand"
    size="lg"
    w="full"
    fontWeight="extrabold"
    py={{ md: '8' }}
    {...props}
  />)
}
