import {
    Box,
    BoxProps,
    Circle,
    createIcon,
    Icon,
    IconButton,
    Stack,
    StackProps,
    useId,
    useRadio,
    useRadioGroup,
    UseRadioProps,
    useStyleConfig,
  } from '@chakra-ui/react'
  import { Children, cloneElement, isValidElement, ReactElement, useMemo } from 'react'
  
  interface RadioCardGroupProps<T> extends Omit<StackProps, 'onChange'> {
    name?: string
    value?: T
    defaultValue?: string
    onChange?: (value: T) => void
  }
  
  export const RadioCardGroup = <T extends string>(props: RadioCardGroupProps<T>) => {
    const { children, name, defaultValue, value, onChange, ...rest } = props
    const { getRootProps, getRadioProps } = useRadioGroup({
      name,
      defaultValue,
      value,
      onChange,
    })
  
    const cards = useMemo(
      () => {
        const childArray = Children.toArray(children)
        return childArray.filter<ReactElement<RadioCardProps>>(isValidElement)
          .map((card) => {
            return cloneElement(card, {
              radioProps: getRadioProps({
                value: card.props.value,
                isDisabled: card.props.radioProps?.isDisabled,
              }),
              choices: childArray.length,
            })
          })
        },
      [children, getRadioProps],
    )
  
    return <Stack {...getRootProps(rest)}>{cards}</Stack>
  }
  
  interface RadioCardProps extends BoxProps {
    value: string
    radioProps?: UseRadioProps
    icon?: ReactElement
    iconVariant?: string
    choices?: number
  }
  
  export const RadioCard = (props: RadioCardProps) => {
    const { radioProps, choices, icon, iconVariant, children, ...rest } = props
    const { getInputProps, getCheckboxProps, getLabelProps, state } = useRadio(radioProps)
    const id = useId(undefined, 'radio-button')
  
    const styles = useStyleConfig('RadioCard', props) as any
    if (choices === 1) {
      styles._checked = { borderColor: 'bg.accent' };
    }
    const inputProps = getInputProps()
    const checkboxProps = getCheckboxProps()
    const labelProps = getLabelProps()
    return (
      <Box
        as="label"
        cursor="pointer"
        {...labelProps}
        sx={{
          '.focus-visible + [data-focus]': {
            boxShadow: 'outline',
            zIndex: 1,
          },
        }}
      >
        <input {...inputProps} aria-labelledby={id} />
        <Box sx={styles} {...checkboxProps} {...rest}>
          <Stack direction="row">
            {icon && iconVariant && (<IconButton icon={icon} variant={iconVariant} color={radioProps?.isDisabled ? 'fg.muted' : undefined} disabled aria-label='' />)}
            <Box flex="1">{children}</Box>
            {!radioProps?.isDisabled && (<>
              {state.isChecked ? (
                <Circle bg="accent" size="4">
                  <Icon as={CheckIcon} boxSize="2.5" color="fg.inverted" />
                </Circle>
              ) : (
                <Circle borderWidth="2px" size="4" />
              )}
            </>)}
          </Stack>
        </Box>
      </Box>
    )
  }
  
  export const CheckIcon = createIcon({
    displayName: 'CheckIcon',
    viewBox: '0 0 12 10',
    path: (
      <polyline
        fill="none"
        strokeWidth="2px"
        stroke="currentColor"
        strokeDasharray="16px"
        points="1.5 6 4.5 9 10.5 1"
      />
    ),
  })