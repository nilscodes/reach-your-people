import { getServerSession } from "next-auth";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { corePublishingApi } from "@/lib/core-publishing-api";
import { getNextAuthOptions } from "@/pages/api/auth/[...nextauth]";
import { Account } from "@/lib/ryp-subscription-api";
import ViewAnnouncementStatistics from "@/components/projects/ViewAnnouncementStatistics";
import { coreRedirectApi } from "@/lib/core-redirect-api";
import Head from "next/head";
import { verifyProjectOwnership } from "@/lib/permissions";
import { Project } from "@/lib/types/Project";

const fallbackAuthor: Account = {
  id: 0,
  displayName: "Deleted",
};

export default function Home({
  announcement,
  project,
  author,
  views,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  return (<>
    <Head>
      <title>{`RYP: Announcement Statistics for ${project.name}`}</title>
    </Head>
    <ViewAnnouncementStatistics announcement={announcement} project={project} author={author} views={views} />
  </>)
}

export async function getServerSideProps(context: any) {
  const session = await getServerSession(
    context.req,
    context.res,
    getNextAuthOptions(context.req, context.res)
  );
  const account = (await coreSubscriptionApi.getAccountById(session?.userId ?? 0)).data;
  const announcementId = context.params.announcementId;
  const announcement = (await corePublishingApi.getAnnouncementById(announcementId)).data;
  const authorId = Number(announcement.announcement.actor.id.split('/').pop());
  const project = (await coreSubscriptionApi.getProject(announcement.projectId as number)).data;
  await verifyProjectOwnership(account, project as Project);
  let views = 0;
  try {
    const shortcode = announcement.shortLink?.split('/').pop();
    if (shortcode) {
      const shortenedUrl = (await coreRedirectApi.getUrlByShortcode(shortcode)).data;
      views = shortenedUrl.views ?? 0;      
    }
  } catch (error) {
    // Shortened URL not found
    console.error(error);
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
      views,
    },
  }
}