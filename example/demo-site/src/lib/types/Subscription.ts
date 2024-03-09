import { DefaultSubscriptionStatus, SubscriptionStatus } from "./SubscriptionStatus";

export type Subscription = {
    projectId: number;
    defaultStatus: DefaultSubscriptionStatus;
    currentStatus: SubscriptionStatus;
    mutedUntil?: string; // Date
    channels?: SubscriptionChannel[];
    favorite?: boolean;
}

type SubscriptionChannel = {
    channelId: number;
    status: SubscriptionStatus;
}