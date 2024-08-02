import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account, ListProjects200Response } from "../../lib/ryp-subscription-api";
import ViewAnnouncement from "@/components/announcements/ViewAnnouncement";
import { corePublishingApi } from "@/lib/core-publishing-api";
import { Announcement } from "@/lib/ryp-publishing-api";
import Head from "next/head";
import { Project } from "@/lib/types/Project";

const fallbackAuthor: Account = {
  id: 0,
  createTime: '',
  displayName: "Deleted",
};

const fallbackProject: ListProjects200Response = {
  id: 0,
  name: "Deleted",
  logo: '',
  url: '',
};

/*
 * Clean up attributes that are not shown to public users before passing the announcement to the component
 */
function getPublicAnnouncement(announcement: Announcement) {
  const publicAnnouncement = { ...announcement };
  delete publicAnnouncement.statistics;
  delete publicAnnouncement.audience;
  return publicAnnouncement;
}

export default function Home({
  announcement,
  project,
  author,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  return (<>
    <Head>
      <title>{`RYP: Announcement details for ${project.name}`}</title>
    </Head>
    <ViewAnnouncement announcement={announcement} project={project} author={author} />
  </>)
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
  const announcementId = context.params.announcementId;
  const fullAnnouncement = (await corePublishingApi.getAnnouncementById(announcementId)).data;
  const announcement = getPublicAnnouncement(fullAnnouncement);
  const authorId = Number(announcement.announcement.actor.id.split('/').pop());
  let project = fallbackProject;
  if (announcement.projectId > 0) {
    try {
      project = (await coreSubscriptionApi.getProject(announcement.projectId as number)).data;
    } catch (error) {
      // Ignore deleted projects, but log the error
      console.error(error);
    }
  }
  let author = fallbackAuthor;
  try {
    author = (await coreSubscriptionApi.getAccountById(authorId)).data;
  } catch (error) {
    // Author not found means they were deleted
  }
  return {
    props: {
      session,
      account,
      announcement,
      project,
      author,
    },
  }
}