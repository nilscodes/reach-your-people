import { Configuration, DefaultApi } from "@/lib/ryp-publishing-api/index"

const apiKey = process.env.IO_VIBRANTNET_RYP_SECURITY_APIKEY;

export const corePublishingApi = new DefaultApi(new Configuration({
  basePath: process.env.PUBLISHING_SERVICE_URL ?? "http://localhost:8072",
  baseOptions: apiKey ? {
    headers: {
      "Authorization": `${apiKey}`,
    },
  } : {},
}));