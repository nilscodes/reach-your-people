import { Configuration, DefaultApi } from "@/lib/ryp-publishing-api/index"

export const corePublishingApi = new DefaultApi(new Configuration({
  basePath: process.env.PUBLISHING_SERVICE_URL ?? "http://localhost:8072",
}));