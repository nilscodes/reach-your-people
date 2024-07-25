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
import { verifyProjectOwnership } from "@/lib/permissions";
import ProjectCategory from "@/lib/types/ProjectCategory";

export default function Home({
  account,
  accountSettings,
  project,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  if (accountSettings?.PUBLISHING_ENABLED === 'true' || project.category === ProjectCategory.SPO) {
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

export async function getServerSideProps(context: any) {
  const session = await getServerSession(
    context.req,
    context.res,
    getNextAuthOptions(context.req, context.res)
  );
  const projectId = context.params.projectid;
  const account = (await coreSubscriptionApi.getAccountById(session?.userId ?? 0)).data;
  const accountSettings = (await coreSubscriptionApi.getSettingsForAccount(session?.userId ?? 0)).data;
  const project = (await coreSubscriptionApi.getProject(projectId)).data as Project;
  await verifyProjectOwnership(account, project);
  return {
    props: {
      session,
      account,
      accountSettings,
      project,
    },
  }
}