export enum TestStatus {
  Waiting = 'waiting',
  Delivered = 'delivered',
  Failed = 'failed',
}

export type TestStatusObject = {
  status: TestStatus;
}