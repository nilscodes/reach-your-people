import { Account } from "@/lib/ryp-subscription-api";
import { Project } from "@/lib/types/Project";
import ProjectViewType from "@/lib/types/ProjectViewType";
import { Subscription } from "@/lib/types/Subscription";
import { SpaceProps } from "@chakra-ui/react";
import { FilterData } from "../filters/CheckboxFilterPopover";

export type VerifiedFilterValue = 'verified' | 'notVerified' | 'verifiedBoth';

export type SubscriptionsDashboardProps = {
  account: Account | null;
  title: string;
  all: boolean;
};

export interface SubscriptionsViewProps extends SpaceProps {
  account: Account | null;
  projects: Project[];
  subscriptions: Subscription[];
  isProjectsLoading: boolean;
  currentCategory: string;
}

export type SubscriptionsHeaderProps = {
  title: string;
  currentType: ProjectViewType;
  onChangeType(val: ProjectViewType): void;
  onSearch(val: string): void;
  searchTerm?: string;
  filterData: FilterData;
  verifiedFilterValue: VerifiedFilterValue;
  onChangeFilter(value: string[]): void;
  onChangeVerifiedFilter(value: VerifiedFilterValue): void;
};