import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../../../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "../../../../lib/ryp-subscription-api";
import Head from "next/head";
import PublishingBeta from "@/components/PublishingBeta";
import { Project } from "@/lib/types/Project";
import { corePublishingApi } from "@/lib/core-publishing-api";
import { Announcement } from "@/lib/ryp-publishing-api";
import ProjectAnnouncementList from "@/components/projects/ProjectAnnouncementList";
import { verifyProjectOwnership } from "@/lib/permissions";

const fallbackAuthor: Account = {
  id: 0,
  displayName: "Deleted",
};

export default function Home({
  account,
  accountSettings,
  project,
  announcements,
  authors,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  if (accountSettings?.PUBLISHING_ENABLED === 'true') {
    return (<>
      <Head>
        <title>RYP: Announcements</title>
      </Head>
      <ProjectAnnouncementList account={account} project={project} announcements={announcements} authors={authors} />
    </>);
  } else {
    return (<>
      <Head>
        <title>RYP: Announcements</title>
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
  const announcements = (await corePublishingApi.listAnnouncementsForProject(projectId)).data;
  const authorIds = Array.from(new Set(announcements.map(announcement => Number(announcement.announcement.actor.id.split('/').pop()))));
  const authors: Record<number, Account> = {};
  for (const authorId of authorIds) {
    try {
      authors[authorId] = (await coreSubscriptionApi.getAccountById(authorId)).data;
    } catch (error) {
      authors[authorId] = fallbackAuthor;
    }
  }
  return {
    props: {
      session,
      account,
      accountSettings,
      project,
      announcements,
      authors,
    },
  }
}