import Head from "next/head";
import CardanoLoginPage from "../../../components/login/CardanoLoginPage";

export default function Home() {
  return (<>
    <Head>
      <title>RYP: Login</title>
    </Head>
    <CardanoLoginPage />
  </>);
}
