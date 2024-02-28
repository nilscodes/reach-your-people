import { getServerSession } from "next-auth";
import { useSession } from "next-auth/react"
import LoginPage from "./login-page";
import { getNextAuthOptions } from "../api/auth/[...nextauth]";

export default function Home() {
  const { data: session } = useSession()

  // if (session?.userId) {
  //   const user = await findUserById(session.userId);
  //   const accounts = await getUserAccounts(session.userId);

  //   return <LoginPage accounts={accounts} user={user} />;
  // }

  return <LoginPage />;
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