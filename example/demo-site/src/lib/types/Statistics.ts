export interface Statistics {
    sent?: Record<string, number>;
    uniqueAccounts?: number;
    explicitSubscribers?: number;
    delivered?: Record<string, number>;
    failures?: Record<string, number>;
    views?: Record<string, number>;
}
