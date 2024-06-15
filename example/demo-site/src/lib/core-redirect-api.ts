import { Configuration, DefaultApi } from "@/lib/ryp-redirect-api/index"

export const coreRedirectApi = new DefaultApi(new Configuration({
  basePath: process.env.REDIRECT_SERVICE_URL ?? "http://localhost:8074",
}));