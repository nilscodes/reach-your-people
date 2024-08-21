import Head from "next/head";
import TermlyPrivacyPolicy from "@/components/legal/TermlyPrivacyPolicy";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Privacy Policy</title>
    </Head>
    <TermlyPrivacyPolicy />
  </>);
}
