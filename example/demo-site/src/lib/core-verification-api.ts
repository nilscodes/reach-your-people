import { Configuration, DefaultApi } from "@/lib/ryp-verification-api/index"

const apiKey = process.env.IO_VIBRANTNET_RYP_SECURITY_APIKEY;

export const coreVerificationApi = new DefaultApi(new Configuration({
  basePath: process.env.VERIFICATION_SERVICE_URL ?? "http://localhost:8070",
  baseOptions: apiKey ? {
    headers: {
      "Authorization": `${apiKey}`,
    },
  } : {},
}));
