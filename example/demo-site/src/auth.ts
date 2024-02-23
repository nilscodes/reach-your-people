import NextAuth from "next-auth"
import type { AuthOptions } from "next-auth"
import Discord from "next-auth/providers/discord"

export const config = {
  theme: {
    logo: "https://vibrantnet.io/logo192.png",
  },
  providers: [
    Discord({
      clientId: process.env.AUTH_DISCORD_CLIENT_ID ?? '',
      clientSecret: process.env.AUTH_DISCORD_CLIENT_SECRET ?? '',
    }),
  ],
  // callbacks: {
  //   authorized({ request, auth }: { request: any; auth: any }) {
  //     const { pathname } = request.nextUrl
  //     if (pathname === "/middleware-example") return !!auth
  //     return true
  //   },
  // },
} satisfies AuthOptions
