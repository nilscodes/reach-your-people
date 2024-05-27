export enum VibrantSyncStatus {
  Unknown = 'Unknown', // Indicates that the sync status is unknown/not started
  InProgress = 'InProgress', // Indicates that the sync is in progress
  CompletedConfirmed = 'CompletedConfirmed', // This is not persisted but used to indicate to the end user the sync just happened
  CompletedNone = 'CompletedNone', // Indicates that the sync was completed but no new wallets were added
  Completed = 'Completed', // Indicates that the sync was completed and new wallets were added
}