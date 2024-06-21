import {
  Button,
  chakra,
  useColorModeValue as mode,
  useRadio,
  UseRadioProps,
  useTheme,
  VisuallyHidden,
} from '@chakra-ui/react'
import { transparentize } from '@chakra-ui/theme-tools'

export type VerifiedPickerButtonProps = UseRadioProps & {
  label?: string
}

export const VerifiedPickerButton = (props: VerifiedPickerButtonProps) => {
  const { value, label } = props
  const { getInputProps, htmlProps, getCheckboxProps, getLabelProps } = useRadio(props)
  const theme = useTheme()

  return (
    <chakra.label {...htmlProps}>
      <chakra.input {...getInputProps()} />
      <Button
        as="span"
        cursor="pointer"
        variant="outline"
        colorScheme="brand"
        color={mode('gray.600', 'gray.400')}
        borderRadius="base"
        borderColor={mode('gray.200', 'gray.600')}
        _checked={{
          color: mode('brand.500', 'brand.200'),
          bg: mode('brand.50', transparentize('brand.200', 0.12)(theme)),
          borderColor: mode('brand.500', 'brand.200'),
          borderWidth: '2px',
        }}
        _focus={{ boxShadow: 'none' }}
        _focusVisible={{ boxShadow: 'outline' }}
        {...getCheckboxProps()}
      >
        {label}
      </Button>
      <VisuallyHidden {...getLabelProps()}>{label} selected</VisuallyHidden>
    </chakra.label>
  )
}
