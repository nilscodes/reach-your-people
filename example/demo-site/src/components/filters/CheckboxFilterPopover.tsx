import { Popover } from '@chakra-ui/react';
import CheckboxFilter, { FilterOption } from './CheckboxFilter';
import { FilterPopoverButton, FilterPopoverContent } from './FilterPopover';
import useFilterState from './useFilterState';
import useTranslation from 'next-translate/useTranslation';

export type FilterData = {
  defaultValue: string[];
  options: FilterOption[];
};

export default function CheckboxFilterPopover({ filterData, onSubmit }: { filterData: FilterData, onSubmit: (value: string[]) => void }) {
  const state = useFilterState({
    defaultValue: filterData.defaultValue,
    onSubmit,
  });
  const { t } = useTranslation('subscriptions');
  return (
    <Popover placement="bottom-start">
      <FilterPopoverButton label={t('typeFilterLabel')} />
      <FilterPopoverContent
        isCancelDisabled={!state.canCancel}
        onClickApply={state.onSubmit}
        onClickCancel={state.onReset}
      >
        <CheckboxFilter
          hideLabel
          value={state.value}
          onChange={(v: string[]) => state.onChange(v)}
          options={filterData.options}
        />
      </FilterPopoverContent>
    </Popover>
  );
};
