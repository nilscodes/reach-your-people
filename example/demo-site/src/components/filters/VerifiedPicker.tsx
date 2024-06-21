import {
  FormControl,
  FormControlProps,
  FormLabel,
  HStack,
  useRadioGroup,
  UseRadioGroupProps,
  Wrap,
} from '@chakra-ui/react'
import { VerifiedPickerButton } from './VerifiedPickerButton'
import useTranslation from 'next-translate/useTranslation'

interface Option {
  label: string
  value: string
}

interface VerifiedPickerProps extends UseRadioGroupProps {
  options: Option[]
  rootProps?: FormControlProps
  hideLabel?: boolean
  label?: string
}

export const VerifiedPicker = (props: VerifiedPickerProps) => {
  const { options, rootProps, hideLabel, label, ...rest } = props
  const { getRadioProps, getRootProps, value } = useRadioGroup(rest)
  const { t } = useTranslation('projects')
  const selectedOption = options.find((option) => option.value == value)

  return (
    <FormControl {...rootProps}>
      {!hideLabel && (
        <FormLabel fontSize="sm" fontWeight="medium">
          {label ?? `Size: ${selectedOption?.label}`}
        </FormLabel>
      )}
      <Wrap {...getRootProps()}>
        {options.map((option) => (
          <VerifiedPickerButton
            key={option.value}
            label={t(option.label)}
            {...getRadioProps({ value: option.value })}
          />
        ))}
      </Wrap>
    </FormControl>
  )
}
