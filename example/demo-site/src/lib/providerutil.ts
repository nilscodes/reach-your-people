import { providerList } from "@/components/ProviderIcons";
import { CreateExternalAccountRequest } from "./ryp-subscription-api";

export function findProviderByType(type: string) {
  return providerList.find((provider) => provider.id === type);
}
