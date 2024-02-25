import type { Metadata } from "next";
import "@fontsource/aldrich"
import "@fontsource/rajdhani/500.css"
import { getServerSession } from 'next-auth';

import SessionProvider from '@/components/SessionProvider';
import Header from "@/components/Header";
import { ClientProviders } from './providers'
import { Box } from "@chakra-ui/react";

export const metadata: Metadata = {
  title: "RYP - Vibrantnet.io",
  description: "Reach Your People - social media and messaging integrations for dReps, DAOs, NFT projects and Stakepool Operators",
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const session = await getServerSession();

  return (
    <html lang="en">
      <head>
        <link rel="icon" href="/favicon.ico" />
        <meta name="twitter:card" content="summary" />
        <meta name="twitter:site" content="@VibrantNet_io" />
        <meta name="twitter:title" content="vibrantnet.io Community Integration System" />
        <meta name="twitter:description" content="Vibrant is a Community Integration Tool for the blockchain Cardano. It comprises a Discord bot and other services to allow you to connect your community by means of blockchain technology." />
        <meta name="twitter:image" content="/logo512.png" />
        <link rel="apple-touch-icon" href="/logo192.png" />
      </head>
      <body>
        <SessionProvider session={session}>
          <ClientProviders>
            <Box as="section" minH="lg">
              <Header />
              {children}
            </Box>
          </ClientProviders>
        </SessionProvider>
      </body>
    </html>
  );
}
