import type { AuthOptions } from "next-auth"
import Discord from "next-auth/providers/discord"
import Github from "next-auth/providers/github"
import CredentialsProvider from "next-auth/providers/credentials"
import { RypSiteApi } from "./lib/api"
import Twitter from "next-auth/providers/twitter"
import Google from "next-auth/providers/google"

const providers: any = [
  // Custom Cardano wallet provider
  CredentialsProvider({
    id: 'cardano',
    name: 'Cardano',
    credentials: {
      stakeAddress: {
        label: "Stake Address",
        type: "text",
        placeholder: "stake1...",
      },
      signature: {
        label: "Signature",
        type: "text",
        placeholder: "{}",
      },
    },
    async authorize(credentials) {
      try {
        const api = new RypSiteApi(`${process.env.NEXTAUTH_URL!}/api`);
        const confirmation = await api.verifySignature(JSON.parse(credentials!.signature), credentials!.stakeAddress);
        console.log('Confirmation', confirmation)
        if (confirmation) {
          return {
            id: credentials!.stakeAddress,
            name: credentials!.stakeAddress.substring(0, 11) + '…' + credentials!.stakeAddress.substring(credentials!.stakeAddress.length - 3),
            email: null,
            image: `${process.env.NEXTAUTH_URL!}/cardano-blue.png`,
          }
        }
        return null
      } catch (e) {
        return null
      }
    },
  }),
];

const enabledProviders = process.env.NEXT_PUBLIC_ENABLED_AUTH_PROVIDERS?.split(',') ?? [];

if (enabledProviders.includes('discord')) {
  providers.push(
    Discord({
      clientId: process.env.AUTH_DISCORD_CLIENT_ID ?? '',
      clientSecret: process.env.AUTH_DISCORD_CLIENT_SECRET ?? '',
    }),
  )
}

if (enabledProviders.includes('github')) {
  providers.push(
    Github({
      clientId: process.env.AUTH_GITHUB_CLIENT_ID ?? '',
      clientSecret: process.env.AUTH_GITHUB_CLIENT_SECRET ?? '',
    }),
  )
}

if (enabledProviders.includes('twitter')) {
  providers.push(
    Twitter({
      clientId: process.env.AUTH_TWITTER_CLIENT_ID ?? '',
      clientSecret: process.env.AUTH_TWITTER_CLIENT_SECRET ?? '',
      version: "2.0",
    }),
  )
}

if (enabledProviders.includes('google')) {
  providers.push(
    Google({
      clientId: process.env.AUTH_GOOGLE_CLIENT_ID ?? '',
      clientSecret: process.env.AUTH_GOOGLE_CLIENT_SECRET ?? '',
    }),
  )
}

export const config = {
  theme: {
    logo: "https://vibrantnet.io/logo192.png",
  },
  providers,
} satisfies AuthOptions
