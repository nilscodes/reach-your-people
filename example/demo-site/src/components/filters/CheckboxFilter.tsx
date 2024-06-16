import {
  Box,
  Checkbox,
  CheckboxGroup,
  CheckboxGroupProps,
  FormLabel,
  Input,
  InputGroup,
  InputRightElement,
  Stack,
  StackProps,
  useColorModeValue as mode,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import { FiSearch } from 'react-icons/fi';

export type FilterOption = {
  label: string;
  value: string;
  count?: number;
};

type CheckboxFilterProps = Omit<CheckboxGroupProps, 'onChange'> & {
  hideLabel?: boolean
  options: FilterOption[],
  label?: string
  onChange?: (value: string[]) => void
  spacing?: StackProps['spacing']
  showSearch?: boolean
};

export default function CheckboxFilter(props: CheckboxFilterProps) {
  const {
    options, label, hideLabel, spacing = '2', showSearch, ...rest
  } = props;
  const { t } = useTranslation('common');

  return (
    <Stack as="fieldset" spacing={spacing}>
      {!hideLabel && (
        <FormLabel fontWeight="semibold" as="legend" mb="0">
          {label}
        </FormLabel>
      )}
      {showSearch && (
        <InputGroup size="md" pb="1">
          <Input
            placeholder={t('filterSearchPlaceholder')}
            rounded="md"
            focusBorderColor={mode('brand.500', 'brand.200')}
          />
          <InputRightElement pointerEvents="none" color="gray.400" fontSize="lg">
            <FiSearch />
          </InputRightElement>
        </InputGroup>
      )}
      <CheckboxGroup {...rest}>
        {options.map((option) => (
          <Checkbox key={option.value} value={option.value} colorScheme="brand">
            <span>{option.label}</span>
            {option.count != null && (
              <Box as="span" color="gray.500" fontSize="sm">
                {' '}
                ({option.count})
              </Box>
            )}
          </Checkbox>
        ))}
      </CheckboxGroup>
    </Stack>);
};
