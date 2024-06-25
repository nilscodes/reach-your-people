import { providerList } from "@/components/ProviderIcons";

export function findProviderByType(type: string) {
  return providerList.find((provider) => provider.id === type);
}