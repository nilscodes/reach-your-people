import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "../../../lib/ryp-subscription-api";
import PublishAnnouncement from "@/components/projects/PublishAnnouncement";
import Head from "next/head";
import PublishingBeta from "@/components/PublishingBeta";
import { Project } from "@/lib/types/Project";

export default function Home({
  account,
  accountSettings,
  project,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession()

  if (session?.userId && account && project !== null) {
    if (accountSettings?.PUBLISHING_ENABLED === 'true') {
      return (<>
        <Head>
          <title>RYP: Publish</title>
        </Head>
        <PublishAnnouncement account={account} project={project} />
      </>);
    } else {
      return (<>
        <Head>
          <title>RYP: Publish</title>
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
  const projectId = context.params.projectid;
  let account: Account | null = null;
  let accountSettings: Record<string, string> = {};
  let project: Project | null = null;
  if (session?.userId) {
    account = (await coreSubscriptionApi.getAccountById(session.userId)).data;
    accountSettings = (await coreSubscriptionApi.getSettingsForAccount(session.userId)).data;
    project = (await coreSubscriptionApi.getProject(projectId)).data as Project;
  }
  return {
    props: {
      session,
      account,
      accountSettings,
      project,
    },
  }
}