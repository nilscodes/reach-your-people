import { coreSubscriptionApi } from "./core-subscription-api";
import { Account } from "./ryp-subscription-api";
import { Project } from "./types/Project";

export async function verifyProjectOwnership(account: Account, project: Project) {
  const ownedProjects = (await coreSubscriptionApi.getProjectsForAccount(account.id!)).data;
  if (!ownedProjects.some(p => p.id === project.id)) {
    throw new Error('Unauthorized');
  }
}