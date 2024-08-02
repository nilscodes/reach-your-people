import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account, GetLinkedExternalAccounts200ResponseInner } from "../../../lib/ryp-subscription-api";
import Head from "next/head";
import AccountsPage from "@/components/AccountsPage";

export default function PremiumAccountHome({
  account,
  linkedAccounts,
  accountSettings,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession()

  if (session?.userId && account) {
    return (<>
      <Head>
        <title>RYP: Premium Account</title>
      </Head>
      <AccountsPage account={account} linkedAccounts={linkedAccounts} accountSettings={accountSettings} currentTab="premium" />
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
  let linkedAccounts: GetLinkedExternalAccounts200ResponseInner[] = [];
  let accountSettings = {};
  if (session?.userId) {
    account = (await coreSubscriptionApi.getAccountById(session.userId)).data;
    linkedAccounts = (await coreSubscriptionApi.getLinkedExternalAccounts(session.userId)).data;
    accountSettings = (await coreSubscriptionApi.getSettingsForAccount(session.userId)).data;
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