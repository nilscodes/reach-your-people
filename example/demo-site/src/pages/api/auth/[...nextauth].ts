import {
  GetServerSidePropsContext,
  NextApiRequest,
  NextApiResponse,
} from "next";
import NextAuth, { Account, NextAuthOptions, User, getServerSession } from "next-auth";
import { config } from "auth"
import { AxiosError } from "axios";
import { coreSubscriptionApi, makeDefaultNotificationsAccountIfNecessary } from "@/lib/core-subscription-api";
import { AdapterUser } from "next-auth/adapters";
import { CreateExternalAccountRequest, GetLinkedExternalAccounts200ResponseInner } from "@/lib/ryp-subscription-api";
import { ExternalAccount } from "@vibrantnet/core";

const getExternalAccountInfoFromProviderAccount = (user: User | AdapterUser, account: Account) => {
  let referenceName = user.name ?? '';
  if (account.provider === 'google' || account.provider === 'github') {
    referenceName = user.email ?? '';
  } else if (account.provider === 'twitter') {
    referenceName = '';
  }
  return {
    type: account.provider,
    referenceId: account.providerAccountId,
    referenceName,
    displayName: user.name ?? 'N/A',
  }
}

function isAxios404Error(e: any) {
  return e instanceof AxiosError && e.response?.status === 404;
}

/**
 * Returns a NextAuthOptions object with extended functionality that requires a request and response object
 * In this specific case, the extended functionality allows for one user multiple accounts
 * @param req A NextRequest
 * @param res A NextResponse
 * @returns A NextAuthOptions object with extended functionality that requires a request and response object
 */
export const getNextAuthOptions = <Req extends Request, Res extends Response>(
  req: NextApiRequest | GetServerSidePropsContext["req"],
  res: NextApiResponse | GetServerSidePropsContext["res"],
) => {
  const extendedOptions: NextAuthOptions = {
    ...config, ...{
      pages: {
        signIn: "/",
        error: "/",
        signOut: "/",
      },
      callbacks: {
        async signIn(params) {
          const { account, user } = params;

          const currentSession = await getServerSession(req, res, extendedOptions);

          const currentUserId = currentSession?.userId;

          // If there is a user logged in already that we recognize,
          // and we have an account that is being signed in with
          // TODO This whole block can likely be simplified (has a bunch of repeating code) and split up into methods afterwards.
          if (account && currentUserId) {
            // Do the account linking
            try {
              const existingAccount = await coreSubscriptionApi.findAccountByProviderAndReferenceId(account.provider, account.providerAccountId);
              if (existingAccount.data.id !== currentUserId) {
                throw new Error("Account is already connected to another user!");
              }
            } catch (e) {
              if (!isAxios404Error(e)) {
                throw e; // Rethrow unexpected errors
              }
            }
            const existingLinkedAccounts = (await coreSubscriptionApi.getLinkedExternalAccounts(currentUserId)).data;
            const newExternalAccountInfo = getExternalAccountInfoFromProviderAccount(user, account);
            if (canAddExternalAccountOfType(existingLinkedAccounts, newExternalAccountInfo)) {
              const externalAccount = (await coreSubscriptionApi.createExternalAccount(newExternalAccountInfo)).data;
              // TODO Prevent linking more than one non-cardano account of the same provider
              const linkedExternalAccount = (await coreSubscriptionApi.linkExternalAccount(currentUserId, externalAccount.id!)).data;
              await makeDefaultNotificationsAccountIfNecessary(existingLinkedAccounts, externalAccount, linkedExternalAccount, currentUserId);
            } else {
              // Update lastConfirmed time of the corresponding linked account if it already exists, as we require revalidation for certain actions
              const linkedAccount = existingLinkedAccounts.find((linkedAccount) => linkedAccount.externalAccount.type === newExternalAccountInfo.type && linkedAccount.externalAccount.referenceId === newExternalAccountInfo.referenceId);
              if (linkedAccount) {
                await coreSubscriptionApi.updateLinkedExternalAccount(currentUserId, linkedAccount.externalAccount.id!, {
                  lastConfirmed: new Date().toISOString(),
                });
              }
            }
            return "/account"; // Prevent the actual login flow, we just linked a new external account and don't need to log anyone in. Redirect to the dashboard instead.
          } else if (account) {
            // If there is no user logged in, but there is an account being signed in with, let's check if we recognize it - if not, create a new account
            try {
              await coreSubscriptionApi.findAccountByProviderAndReferenceId(account.provider, account.providerAccountId);
            } catch (e) {
              if (isAxios404Error(e)) {
                const newAccount = await coreSubscriptionApi.createAccount({
                  displayName: user.name ?? 'Unknown',
                });

                const externalAccount = (await coreSubscriptionApi.createExternalAccount(getExternalAccountInfoFromProviderAccount(user, account))).data;

                const linkedExternalAccount = (await coreSubscriptionApi.linkExternalAccount(newAccount.data.id!, externalAccount.id!)).data;

                await makeDefaultNotificationsAccountIfNecessary([], externalAccount, linkedExternalAccount, newAccount.data.id!);
              } else {
                throw e; // Rethrow unexpected errors
              }
            }
          }

          return true;
        },

        async jwt(params) {
          const { token, account, user } = params;

          // If there is an account for which we are generating JWT for (e.g on sign in)
          // then attach our userId to the token
          if (account) {
            const existingAccount = await coreSubscriptionApi.findAccountByProviderAndReferenceId(account.provider, account.providerAccountId);
            token.userId = existingAccount.data.id;
          }

          return token;
        },

        async session(params) {
          const { session, token } = params;
          // Attach the user id from our table to session to be able to link accounts later on sign in
          // when we make the call to getServerSession
          session.userId = token.userId;
          if (session.user && session.user.email === undefined) { // Set email to null if it is undefined so it can be serialized in getServerSideProps
            session.user.email = null;
          }
          return session;
        },
      },
    }
  };

  return extendedOptions;
};

function canAddExternalAccountOfType(existingLinkedAccounts: GetLinkedExternalAccounts200ResponseInner[], newExternalAccountInfo: CreateExternalAccountRequest) {
  return !existingLinkedAccounts.some((linkedAccount) => newExternalAccountInfo.type === linkedAccount.externalAccount.type)
    || (newExternalAccountInfo.type === 'cardano' && !existingLinkedAccounts.some((linkedAccount) => linkedAccount.externalAccount.type === 'cardano' && linkedAccount.externalAccount.referenceId === newExternalAccountInfo.referenceId));
}

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  return NextAuth(req, res, getNextAuthOptions(req, res));
};
