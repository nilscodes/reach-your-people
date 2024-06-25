export enum FirstStepsItems {
  ConnectNotification = 1 << 0,  // 1
  ConnectWallet = 1 << 1,  // 2
  SubscribeExplicitly = 1 << 2,  // 4
  ReferFriend = 1 << 3,  // 8
  Completed = 1 << 4,  // 16
  Cancelled = 1 << 5,  // 32
}

export function enumToBitmask(items: FirstStepsItems[]): number {
  return items.reduce((bitmask, item) => bitmask | item, 0);
}

export function bitmaskToEnum(bitmask: number): FirstStepsItems[] {
  return Object.values(FirstStepsItems).filter((item) => typeof item === 'number' && (bitmask & (item as number)) !== 0) as FirstStepsItems[];
}

