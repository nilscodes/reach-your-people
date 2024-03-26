import axios from "axios";

// Create an axios instance with the base URL of the phone-verify service based on the environment variable PHONE_VERIFY_SERVICE_URL
export const phoneVerifyApi = axios.create({
  baseURL: process.env.PHONE_VERIFY_SERVICE_URL ?? "http://localhost:3010",
});