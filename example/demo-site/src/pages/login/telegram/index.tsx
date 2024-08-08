import Head from "next/head";
import TelegramLoginPage from "@/components/login/TelegramLoginPage";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Login</title>
    </Head>
    <TelegramLoginPage />
  </>);
}
