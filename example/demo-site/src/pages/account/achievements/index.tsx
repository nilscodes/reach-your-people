import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account, GetLinkedExternalAccounts200ResponseInner } from "../../../lib/ryp-subscription-api";
import Head from "next/head";
import AccountsPage from "@/components/AccountsPage";
import { corePointsApi } from "@/lib/core-points-api";
import { createAchievements } from "@/lib/achievements";
import { Achievement } from "@/lib/types/Achievement";

export default function WalletsHome({
  account,
  achievements,
  linkedAccounts,
  accountSettings,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession()

  if (session?.userId && account) {
    return (<>
      <Head>
        <title>RYP: Achievements</title>
      </Head>
      <AccountsPage account={account} linkedAccounts={linkedAccounts} accountSettings={accountSettings} currentTab="achievements" achievements={achievements} />
    </>);
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
  let achievements: Achievement[] = [];
  let linkedAccounts: GetLinkedExternalAccounts200ResponseInner[] = [];
  let accountSettings = {};
  if (session?.userId) {
    account = (await coreSubscriptionApi.getAccountById(session.userId)).data;
    linkedAccounts = (await coreSubscriptionApi.getLinkedExternalAccounts(session.userId)).data;
    accountSettings = (await coreSubscriptionApi.getSettingsForAccount(session.userId)).data;
    try {
      const pointClaims = (await corePointsApi.getPointClaimsForAccount(session.userId)).data;
      achievements = createAchievements(pointClaims);
    } catch (e) {
      // Ignore missing claims, we display an error in the UI - but don't block the page
      // TODO: Log this error to a monitoring service like prometheus
      console.error(e);
    }
  }
  return {
    props: {
      session,
      account,
      linkedAccounts,
      accountSettings,
      achievements,
    },
  }
}