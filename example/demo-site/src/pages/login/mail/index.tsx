import Head from "next/head";
import EmailLoginPage from "../../../components/login/EmailLoginpage";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Login</title>
    </Head>
    <EmailLoginPage />
  </>);
}
