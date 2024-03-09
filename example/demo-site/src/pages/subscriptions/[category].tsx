import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "@/lib/ryp-api";
import { useRouter } from "next/router";
import { SubscriptionsDashboard } from "@/components/subscriptions/SubscriptionsDashboard";

export default function SubscriptionCategory({
  account,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const router = useRouter();
  // Access category from the route
  const { category } = router.query;

  return <SubscriptionsDashboard account={account} title={category as string} all />;
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