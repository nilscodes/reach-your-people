import { Html, Head, Main, NextScript } from 'next/document'

export default function Document() {
  return (
    <Html lang="en">
      <Head>
      <link rel="icon" href="/favicon.ico" />
        <meta name="twitter:card" content="summary" />
        <meta name="twitter:site" content="@VibrantNet_io" />
        <meta name="twitter:title" content="vibrantnet.io Community Integration System" />
        <meta name="twitter:description" content="Vibrant is a Community Integration Tool for the blockchain Cardano. It comprises a Discord bot and other services to allow you to connect your community by means of blockchain technology." />
        <meta name="twitter:image" content="/logo512.png" />
        <link rel="apple-touch-icon" href="/logo192.png" />
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  )
}

