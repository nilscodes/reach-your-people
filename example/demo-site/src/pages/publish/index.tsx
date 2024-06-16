import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "../../lib/ryp-subscription-api";
import ProjectsHomepage from "@/components/projects/ProjectsHomepage";
import Head from "next/head";
import PublishingBeta from "@/components/PublishingBeta";

export default function Home({
  account,
  accountSettings,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession();

  if (session?.userId && account) {
    if (accountSettings?.PUBLISHING_ENABLED === 'true') {
      return (<>
        <Head>
          <title>RYP: Publishing</title>
        </Head>
        <ProjectsHomepage account={account} />
      </>);
    } else {
      return (<>
        <Head>
          <title>RYP: Publishing</title>
        </Head>
        <PublishingBeta />
      </>);
    }
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
  let accountSettings: Record<string, string> = {};
  if (session?.userId) {
    account = (await coreSubscriptionApi.getAccountById(session.userId)).data;
    accountSettings = (await coreSubscriptionApi.getSettingsForAccount(session.userId)).data;
  }
  return {
    props: {
      session,
      account,
      accountSettings,
    },
  }
}