import {
  Box,
  ButtonGroup,
  ButtonGroupProps,
  IconButton,
  IconButtonProps,
  useId,
  useRadio,
  useRadioGroup,
  UseRadioProps,
} from '@chakra-ui/react';
import {
  Children, cloneElement, isValidElement, ReactElement, useMemo,
} from 'react';

interface RadioIconButtonGroupProps<T>
  extends Omit<ButtonGroupProps, 'onChange' | 'variant' | 'isAttached'> {
  name?: string
  value?: T
  defaultValue?: string
  onChange?: (value: T) => void
}

export const RadioIconButtonGroup = <T extends string>(props: RadioIconButtonGroupProps<T>) => {
  const {
    children, name, defaultValue, value, onChange, ...rest
  } = props;
  const { getRootProps, getRadioProps } = useRadioGroup({
    name,
    defaultValue,
    value,
    onChange,
  });

  const buttons = useMemo(
    () => Children.toArray(children)
      .filter<ReactElement<RadioIconButtonProps>>(isValidElement)
      .map((button, index, array) => {
        const isFirstItem = index === 0;
        const isLastItem = array.length === index + 1;

        const styleProps = {
          ...(isFirstItem && !isLastItem ? { borderRightRadius: 0 } : {}),
          ...(!isFirstItem && isLastItem ? { borderLeftRadius: 0 } : {}),
          ...(!isFirstItem && !isLastItem ? { borderRadius: 0 } : {}),
          ...(!isLastItem ? { mr: '-px' } : {}),
        };

        return cloneElement(button, {
          ...styleProps,
          radioProps: getRadioProps({
            value: button.props.value,
            disabled: props.isDisabled || button.props.isDisabled,
          }),
        });
      }),
    [children, getRadioProps, props.isDisabled],
  );
  return (
    <ButtonGroup isAttached variant="secondary" {...getRootProps(rest)}>
      {buttons}
    </ButtonGroup>
  );
};

interface RadioIconButtonProps extends IconButtonProps {
  value: string
  radioProps?: UseRadioProps
}

export const RadioIconButton = (props: RadioIconButtonProps) => {
  const { radioProps, ...rest } = props;
  const { getInputProps, getCheckboxProps, getLabelProps } = useRadio(radioProps);
  const id = useId(undefined, 'radio-button');

  const inputProps = getInputProps();
  const checkboxProps = getCheckboxProps();
  const labelProps = getLabelProps();

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
      <IconButton id={id} as="div" _focus={{ boxShadow: 'none' }} {...checkboxProps} {...rest} />
    </Box>
  );
};
