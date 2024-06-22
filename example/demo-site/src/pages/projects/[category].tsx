import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account } from "../../lib/ryp-subscription-api";
import { useRouter } from "next/router";
import { SubscriptionsDashboard } from "@/components/subscriptions/SubscriptionsDashboard";
import { Project } from "@/lib/types/Project";
import ProjectPage from "@/components/subscriptions/ProjectPage";
import Head from "next/head";

export default function SubscriptionCategory({
  account,
  project,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const router = useRouter();
  const { category } = router.query;

  if (project) {
    return (<>
      <Head>
        <title>{`RYP: ${project.name}`}</title>
      </Head>
      <ProjectPage account={account} project={project} />
    </>)
  }
  return (<>
    <Head>
      <title>RYP: Projects</title>
    </Head>
    <SubscriptionsDashboard account={account} title={category as string} all />;
  </>)
}

export async function getServerSideProps(context: any) {
  const session = await getServerSession(
    context.req,
    context.res,
    getNextAuthOptions(context.req, context.res)
  );
  const categoryOrProject = context.params.category;

  let account: Account | null = null;
  if (session?.userId) {
    account = (await coreSubscriptionApi.getAccountById(session.userId)).data;
  }
  let project: Project | null = null;
  // If category is a number, show a project details page
  if (categoryOrProject && +categoryOrProject > 0) {
    project = (await coreSubscriptionApi.getProject(categoryOrProject)).data as Project;
  }
  return {
    props: {
      session,
      account,
      project,
    },
  }
}