import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account, GetLinkedExternalAccounts200ResponseInner } from "../../lib/ryp-subscription-api";
import LinkedAccounts from "@/components/LinkedAccounts";
import { VibrantSyncStatus } from "@/lib/types/VibrantSyncStatus";
import { VibrantIntegrationApi } from "@/lib/integration-vibrant-api";
import { getWalletDisplayName } from "@/lib/cardano";
import { BlockchainType } from "@vibrantnet/core";
import Head from "next/head";

const VIBRANT_INTEGRATION_URL = process.env.VIBRANT_INTEGRATION_URL ?? "http://localhost:8080";

export default function Home({
  account,
  linkedAccounts,
  accountSettings,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession()

  if (session?.userId && account && accountSettings) {
    return (<>
      <Head>
        <title>RYP: Dashboard</title>
      </Head>
      <LinkedAccounts account={account} linkedAccounts={linkedAccounts} accountSettings={accountSettings} />;
    </>)
  }

  return <AccessDenied />;
}

export async function getServerSideProps(context: any) {
  const session = await getServerSession(
    context.req,
    context.res,
    getNextAuthOptions(context.req, context.res)
  );
  let account: Account | null = null;
  let linkedAccounts: GetLinkedExternalAccounts200ResponseInner[] = [];
  let accountSettings: Record<string, string> | null = null;
  if (session?.userId) {
    const accountInfoCalls = await Promise.all([
      coreSubscriptionApi.getAccountById(session.userId),
      coreSubscriptionApi.getLinkedExternalAccounts(session.userId),
      coreSubscriptionApi.getSettingsForAccount(session.userId),
    ]);
    account = accountInfoCalls[0].data;
    linkedAccounts = accountInfoCalls[1].data;
    accountSettings = accountInfoCalls[2].data;
    const changesFromMigration = await checkVibrantMigrationStatus(account, linkedAccounts, accountSettings);
    accountSettings = changesFromMigration.accountSettings;
    linkedAccounts = changesFromMigration.linkedAccounts;
  }
  return {
    props: {
      session,
      account,
      linkedAccounts,
      accountSettings,
    },
  }
}

/*
 * Check if the user has a linked Discord account and if so, check if they have any verified Cardano wallets on Vibrant.
 * If they do, link them to the user's account and update the account settings to reflect the migration status.
 */
async function checkVibrantMigrationStatus(account: Account, linkedAccounts: GetLinkedExternalAccounts200ResponseInner[], accountSettings: Record<string, string>) {
  const discordAccount = linkedAccounts.find((account) => account.externalAccount.type === 'discord');
  if (discordAccount) {
    let syncStatus: VibrantSyncStatus = (accountSettings['VIBRANT_SYNC_STATUS'] as VibrantSyncStatus) ?? VibrantSyncStatus.Unknown;
    if (syncStatus === VibrantSyncStatus.Unknown) {
      coreSubscriptionApi.updateAccountSetting(account.id!, 'VIBRANT_SYNC_STATUS', { name: 'VIBRANT_SYNC_STATUS', value: VibrantSyncStatus.InProgress });
      const vibrantIntegrationApi = new VibrantIntegrationApi(VIBRANT_INTEGRATION_URL);
      try {
        const vibrantVerifications = await vibrantIntegrationApi.getVerificationsForDiscordUser(discordAccount.externalAccount.referenceId);
        const verifiedVibrantCardanoWallets = vibrantVerifications.filter((verification) => verification.blockchain === BlockchainType.CARDANO && verification.cardanoStakeAddress && verification.confirmed);
        let newlyAddedWallets = 0;
        for (const verification of verifiedVibrantCardanoWallets) {
          const existingWalletWithStakeAddress = linkedAccounts.find((linkedAccount) => linkedAccount.externalAccount.referenceId === verification.cardanoStakeAddress);
          if (!existingWalletWithStakeAddress) {
            const displayName = getWalletDisplayName(verification.cardanoStakeAddress!);
            const externalAccount = (await coreSubscriptionApi.createExternalAccount({
              type: 'cardano',
              referenceId: verification.cardanoStakeAddress!,
              referenceName: displayName,
              displayName,
            })).data;
            await coreSubscriptionApi.linkExternalAccount(account.id!, externalAccount.id!);
            newlyAddedWallets += 1;
          }
        }
        syncStatus = newlyAddedWallets > 0 ? VibrantSyncStatus.Completed : VibrantSyncStatus.CompletedNone;
        coreSubscriptionApi.updateAccountSetting(account.id!, 'VIBRANT_SYNC_STATUS', { name: 'VIBRANT_SYNC_STATUS', value: syncStatus });
        if (newlyAddedWallets > 0) {
          const newLinkedAccounts = (await coreSubscriptionApi.getLinkedExternalAccounts(account.id!)).data;
          return { accountSettings: { ...accountSettings, VIBRANT_SYNC_STATUS: VibrantSyncStatus.CompletedConfirmed }, linkedAccounts: newLinkedAccounts };
        }
      } catch (error) {
        console.error(error);
        // Ignore any errors
      }
    }
    return { accountSettings: { ...accountSettings, VIBRANT_SYNC_STATUS: syncStatus }, linkedAccounts };
  }
  return { accountSettings, linkedAccounts };
}
