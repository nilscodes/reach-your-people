import Head from "next/head";
import TermlyTermsAndConditions from "@/components/legal/TermlyTermsAndConditions";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Terms and Conditions</title>
    </Head>
    <TermlyTermsAndConditions />
  </>);
}
