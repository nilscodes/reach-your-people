import { getServerSession } from "next-auth";
import LoginPage from "../../components/login/LoginPage";
import { getNextAuthOptions } from "../api/auth/[...nextauth]";
import Head from "next/head";

export default function Home() {
  // const { data: session } = useSession()

  // if (session?.userId) {
  //   const user = await findUserById(session.userId);
  //   const accounts = await getUserAccounts(session.userId);

  //   return <LoginPage accounts={accounts} user={user} />;
  // }
  return (<>
    <Head>
      <title>RYP: Login</title>
    </Head>
    <LoginPage />
  </>);
}

export async function getServerSideProps(context: any) {
  return {
    props: {
      session: await getServerSession(
        context.req,
        context.res,
        getNextAuthOptions(context.req, context.res)
      ),
    },
  }
}