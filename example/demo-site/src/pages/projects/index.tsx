import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import AccessDenied from "@/components/AccessDenied";
import { coreSubscriptionApi } from "@/lib/core-subscription-api";
import { InferGetServerSidePropsType } from "next";
import { Account, GetLinkedExternalAccounts200ResponseInner } from "../../lib/ryp-subscription-api";
import ProjectsHomepage from "@/components/projects/ProjectsHomepage";
import Head from "next/head";

export default function Home({
  account,
}: InferGetServerSidePropsType<typeof getServerSideProps>) {
  const { data: session } = useSession();

  if (session?.userId && account) {
    return (<>
      <Head>
        <title>RYP: Projects</title>
      </Head>
      <ProjectsHomepage account={account} />
    </>);
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