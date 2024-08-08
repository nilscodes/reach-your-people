import type { AuthOptions } from "next-auth"
import Discord from "next-auth/providers/discord"
import Github from "next-auth/providers/github"
import CredentialsProvider from "next-auth/providers/credentials"
import Email from "next-auth/providers/email"
import { RypSiteApi } from "./lib/api"
import Twitter from "next-auth/providers/twitter"
import Google from "next-auth/providers/google"
import { getWalletDisplayName } from "./lib/cardanoutil"
import { DynamoDB, DynamoDBClientConfig } from "@aws-sdk/client-dynamodb"
import { DynamoDBDocument } from "@aws-sdk/lib-dynamodb"
import { DynamoDBAdapter } from "@auth/dynamodb-adapter"
import type { Adapter } from 'next-auth/adapters';
import { sendVerificationRequest } from "./lib/email"
import { AuthDataValidator, objectToAuthDataMap } from "@telegram-auth/server"

let customAdapter: Adapter | undefined = undefined;

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
      txCbor: {
        label: "Transaction CBOR",
        type: "text",
        placeholder: "AABBCCDD...",
      },
    },
    async authorize(credentials) {
      try {
        const api = new RypSiteApi(`${process.env.NEXTAUTH_URL!}/api`);
        let confirmation = false;
        if (credentials!.txCbor) {
          confirmation = await api.verifyTransaction(credentials!.txCbor);
        } else {
          confirmation = await api.verifySignature(JSON.parse(credentials!.signature), credentials!.stakeAddress);
        }
        console.log('Confirmation', confirmation)
        if (confirmation) {
          return {
            id: credentials!.stakeAddress,
            name: getWalletDisplayName(credentials!.stakeAddress),
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

if (enabledProviders.includes('telegram')) {
  providers.push(
    CredentialsProvider({
			id: 'telegram',
			name: 'Telegram',
			credentials: {},
			async authorize(credentials, req) {
				const validator = new AuthDataValidator({ botToken: `${process.env.AUTH_TELEGRAM_BOT_TOKEN}` });

				const data = objectToAuthDataMap(req.query || {});
				const user = await validator.validate(data);

				if (user.id && user.first_name) {
					return {
						id: user.id.toString(),
            username: user.username,
						name: [user.first_name, user.last_name || ''].join(' '),
						image: user.photo_url,
					};
				}

				return null;
			},
		}),
  );
}

if (enabledProviders.includes('email')) {
  providers.push(
    Email({
      from: process.env.AUTH_EMAIL_FROM,
      server: {
        host: "smtp.sendgrid.net",
        port: 587,
        auth: {
          user: "apikey",
          pass: process.env.AUTH_SENDGRID_KEY,
        }
      },
      maxAge: 30 * 60, // 30 minutes magic link validity
      sendVerificationRequest: sendVerificationRequest,
    }),
  )

  const config: DynamoDBClientConfig = {
    credentials: {
      accessKeyId: process.env.AUTH_DYNAMODB_ID!,
      secretAccessKey: process.env.AUTH_DYNAMODB_SECRET!,
    },
    region: process.env.AUTH_DYNAMODB_REGION,
  }
   
  const client = DynamoDBDocument.from(new DynamoDB(config), {
    marshallOptions: {
      convertEmptyValues: true,
      removeUndefinedValues: true,
      convertClassInstanceToMap: true,
    },
  })
  customAdapter = DynamoDBAdapter(client, {
    tableName: process.env.AUTH_DYNAMODB_TABLE!,
  }) as Adapter;
}

export const config = {
  adapter: customAdapter,
  theme: {
    logo: "https://vibrantnet.io/logo192.png",
  },
  providers,
  session: {
    strategy: 'jwt',
  }
} satisfies AuthOptions
