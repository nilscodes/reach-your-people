import { Account } from "./ryp-api";

type JwtPayload = {
  userId?: Account["id"];
};

declare module "next-auth" {
  /**
   * Returned by `useSession`, `getSession` and received as a prop on the `SessionProvider` React Context
   */
  interface Session {
    userId?: Account["id"];
  }
}

declare module "next-auth/jwt" {
  /** Returned by the `jwt` callback and `getToken`, when using JWT sessions */
  interface JWT extends JwtPayload {
    [k in JwtPayload]: JwtPayload[k];
  }
}
