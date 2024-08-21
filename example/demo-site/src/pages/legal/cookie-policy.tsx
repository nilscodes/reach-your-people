import Head from "next/head";
import TermlyCookiePolicy from "@/components/legal/TermlyCookiePolicy";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Terms and Conditions</title>
    </Head>
    <TermlyCookiePolicy />
  </>);
}
