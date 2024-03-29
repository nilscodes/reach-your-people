import { Configuration, DefaultApi } from "@/lib/ryp-subscription-api/index"

export const coreSubscriptionApi = new DefaultApi(new Configuration({
  basePath: process.env.SUBSCRIPTION_SERVICE_URL ?? "http://localhost:8071",
}));