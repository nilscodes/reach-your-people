import Head from "next/head";
import EmailUnsubscribePage from "@/components/login/EmailUnsubscribePage";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Unsubscribe</title>
    </Head>
    <EmailUnsubscribePage />
  </>);
}
