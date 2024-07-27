import { Configuration, DefaultApi } from "@/lib/ryp-points-api/index"

const apiKey = process.env.IO_VIBRANTNET_RYP_SECURITY_APIKEY;

export const corePointsApi = new DefaultApi(new Configuration({
  basePath: process.env.POINTS_SERVICE_URL ?? "http://localhost:8075",
  baseOptions: apiKey ? {
    headers: {
      "Authorization": `${apiKey}`,
    },
  } : {},
}));