import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "../../lib/ryp-subscription-api";
import { SubscriptionsHomepage } from "@/components/subscriptions/SubscriptionsHomepage";
import Head from "next/head";

export default function Subscriptions({
  account,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession()

  return (<>
    <Head>
      <title>RYP: Projects</title>
    </Head>
    <SubscriptionsHomepage account={account} subscriptions={[]} />
  </>);
}

export async function getServerSideProps(context: any) {
  const session = await getServerSession(
    context.req,
    context.res,
    getNextAuthOptions(context.req, context.res)
  );
  let account: Account | null = null;
  if (session?.userId) {
    account = (await coreSubscriptionApi.getAccountById(session.userId)).data;
  }
  return {
    props: {
      session,
      account,
    },
  }
}