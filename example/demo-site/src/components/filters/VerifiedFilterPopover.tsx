import { Popover } from "@chakra-ui/react";
import useFilterState from "./useFilterState";
import { FilterPopoverButton, FilterPopoverContent } from "./FilterPopover";
import { VerifiedPicker } from "./VerifiedPicker";
import useTranslation from "next-translate/useTranslation";
import { VerifiedFilterValue } from "../subscriptions/SubscriptionsDashboard";

export const verifiedFilter = {
  defaultValue: 'verified',
  options: [
    { label: 'verified', value: 'verified' },
    { label: 'notVerified', value: 'notVerified' },
    { label: 'verifiedBoth', value: 'verifiedBoth' },
  ],
}

export const VerifiedFilterPopover = ({ defaultValue, onSubmit }: { defaultValue: string, onSubmit: (value: VerifiedFilterValue) => void }) => {
  const state = useFilterState({
    defaultValue,
    onSubmit: () => {
      onSubmit(state.value as VerifiedFilterValue)
    },
  })
  const { t } = useTranslation('projects')

  return (
    <Popover placement="bottom-start">
      <FilterPopoverButton label={t('verificationStatus')} />
      <FilterPopoverContent
        isCancelDisabled={!state.canCancel}
        onClickApply={state.onSubmit}
        onClickCancel={state.onReset}
      >
        <VerifiedPicker
          hideLabel
          value={state.value}
          onChange={state.onChange}
          options={verifiedFilter.options}
        />
      </FilterPopoverContent>
    </Popover>
  )
}