import { Configuration, DefaultApi } from "@/lib/ryp-points-api/index"

export const corePointsApi = new DefaultApi(new Configuration({
  basePath: process.env.POINTS_SERVICE_URL ?? "http://localhost:8075",
}));