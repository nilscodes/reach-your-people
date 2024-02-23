import NextAuth from "next-auth/next";
import { config } from "auth"

export const handler = NextAuth(config);

export { handler as GET, handler as POST }