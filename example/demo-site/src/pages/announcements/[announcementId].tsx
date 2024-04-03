import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "../../lib/ryp-subscription-api";
import ViewAnnouncement from "@/components/announcements/ViewAnnouncement";
import { corePublishingApi } from "@/lib/core-publishing-api";

const fallbackAuthor: Account = {
  id: 0,
  displayName: "Deleted",
};

export default function Home({
  announcement,
  project,
  author,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  return <ViewAnnouncement announcement={announcement} project={project} author={author} />;
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
  const announcementId = context.params.announcementid;
  const announcement = (await corePublishingApi.getAnnouncementById(announcementId)).data;
  const authorId = Number(announcement.announcement.actor.id.split('/').pop());
  const project = (await coreSubscriptionApi.getProject(announcement.projectId as number)).data;
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