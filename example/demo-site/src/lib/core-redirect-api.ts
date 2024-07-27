import { Configuration, DefaultApi } from "@/lib/ryp-redirect-api/index"

const apiKey = process.env.IO_VIBRANTNET_RYP_SECURITY_APIKEY;

export const coreRedirectApi = new DefaultApi(new Configuration({
  basePath: process.env.REDIRECT_SERVICE_URL ?? "http://localhost:8074",
  baseOptions: apiKey ? {
    headers: {
      "Authorization": `${apiKey}`,
    },
  } : {},
}));