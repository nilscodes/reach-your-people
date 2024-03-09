import { Popover } from '@chakra-ui/react';
import CheckboxFilter, { FilterOption } from './CheckboxFilter';
import { FilterPopoverButton, FilterPopoverContent } from './FilterPopover';
import useFilterState from './useFilterState';

export type FilterData = {
  defaultValue: string[];
  options: FilterOption[];
};

export default function CheckboxFilterPopover({ filterData, onSubmit }: { filterData: FilterData, onSubmit: (value: string[]) => void }) {
  const state = useFilterState({
    defaultValue: filterData.defaultValue,
    onSubmit,
  });
  return (
    <Popover placement="bottom-start">
      <FilterPopoverButton label="Type" />
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
