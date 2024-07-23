import { Configuration, DefaultApi } from "@/lib/ryp-verification-api/index"

export const coreVerificationApi = new DefaultApi(new Configuration({
  basePath: process.env.VERIFICATION_SERVICE_URL ?? "http://localhost:8070",
}));
