import { Configuration, DefaultApi } from "@/lib/ryp-billing-api/index"

const apiKey = process.env.IO_VIBRANTNET_RYP_SECURITY_APIKEY;

export const coreBillingApi = new DefaultApi(new Configuration({
  basePath: process.env.BILLING_SERVICE_URL ?? "http://localhost:8076",
  baseOptions: apiKey ? {
    headers: {
      "Authorization": `${apiKey}`,
    },
  } : {},
}));