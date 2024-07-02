import { Html, Head, Main, NextScript } from 'next/document'

const description = "RYP is a communication and announcement hub for Cardano and other blockchains. It allows users to connect their social media and wallet information and receive notifications from verified projects through the channels they prefer, while ensuring security, privacy and providing a great user experience.";

export default function Document() {
  return (
    <Html lang="en">
      <Head>
        <link rel="icon" href="/favicon.ico" />
        <meta name="description" content={description} />
        <meta name="twitter:card" content="summary" />
        <meta name="twitter:site" content="@VibrantNet_io" />
        <meta name="twitter:title" content="Reach Your People (RYP)" />
        <meta name="twitter:description" content={description} />
        <meta name="twitter:image" content={`${process.env.NEXT_PUBLIC_API_URL}/logo512.png`} />
        <link rel="apple-touch-icon" href={`${process.env.NEXT_PUBLIC_API_URL}/logo192.png`} />
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  )
}

