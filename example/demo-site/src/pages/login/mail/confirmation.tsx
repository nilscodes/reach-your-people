import Head from "next/head";
import EmailConfirmationPage from "@/components/login/EmailConfirmationPage";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Login</title>
    </Head>
    <EmailConfirmationPage />
  </>);
}
