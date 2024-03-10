import axios from "axios";
import { Project, ProjectCreationRequest } from "./types/Project";
import ProjectCategory from "./types/ProjectCategory";
import { Subscription } from "./types/Subscription";
import { DefaultSubscriptionStatus, SubscriptionStatus } from "./types/SubscriptionStatus";

function getRandomDelay(): Promise<void> {
  const delay = Math.random() * (500 - 30) + 30;
  return new Promise((resolve) => { setTimeout(resolve, delay); });
}

export type NonceResponse = {
  nonce: string;
}

export class RypSiteApi {
  constructor(private readonly baseUrl: string) {
    axios.defaults.withCredentials = true;
    this.baseUrl = baseUrl;
  }

  async createNonce(userAddress: string, stakeAddress: string): Promise<NonceResponse> {
    return (await axios.post(`${this.baseUrl}/wallets/nonce`, { userAddress, stakeAddress })).data;
  }

  async verifySignature(signature: string, stakeAddress: string): Promise<boolean> {
    return (await axios.post(`${this.baseUrl}/wallets/verify`, { signature, stakeAddress })).data;
  }

  async getBestHandle(stakeAddress: string): Promise<string> {
    return (await axios.get(`${this.baseUrl}/wallets/besthandle/${stakeAddress}`)).data;
  }

  async unlinkExternalAccount(externalAccountId: number): Promise<void> {
    axios.delete(`${this.baseUrl}/account/externalaccounts/${externalAccountId}`);
  }

  async getProjectsForAccount(): Promise<Project[]> {
    return (await axios.get(`${this.baseUrl}/account/projects`)).data;
  }

  async addNewProject(project: ProjectCreationRequest): Promise<Project> {
    return (await axios.post(`${this.baseUrl}/projects`, project)).data;
  }

  async getProjects(): Promise<Project[]> {
    // await getRandomDelay();
    // return [{
    //   id: 1,
    //   name: "Minswap",
    //   category: ProjectCategory.DeFi,
    //   logo: 'https://www.gitbook.com/cdn-cgi/image/width=36,dpr=2,height=36,fit=contain,format=auto/https%3A%2F%2F245681299-files.gitbook.io%2F~%2Ffiles%2Fv0%2Fb%2Fgitbook-legacy-files%2Fo%2Fspaces%252F-Mb6kABTQvTeYDjx9qXI%252Favatar-1622562044274.png%3Fgeneration%3D1622562044559003%26alt%3Dmedia',
    //   url: "https://minswap.org",
    //   tags: ["tag1", "tag2"],
    //   registrationTime: "2021-01-01T00:00:00Z",
    //   verified: true,
    // },
    // {
    //   id: 2,
    //   name: "SpaceBudz",
    //   category: ProjectCategory.NFT,
    //   logo: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA4MDYuMDUgNTc1LjI1Ij48ZGVmcz48c3R5bGU+LmNscy0xe2ZpbGw6I2ZmZjt9LmNscy0ye2ZpbGw6IzhiNWNmNjt9LmNscy0ze2ZpbGw6IzgwNjRmNDt9PC9zdHlsZT48L2RlZnM+PGcgaWQ9IkxheWVyXzIiIGRhdGEtbmFtZT0iTGF5ZXIgMiI+PGcgaWQ9IkxheWVyXzEtMiIgZGF0YS1uYW1lPSJMYXllciAxIj48cGF0aCBjbGFzcz0iY2xzLTEiIGQ9Ik03NTYuMjcsMjM1LjU2YTE0Ny43MywxNDcuNzMsMCwwLDEsOC42NiwxMTAuNzgsNzUuNDIsNzUuNDIsMCwwLDAsMjAuNjMtNTcuMjFDNzgzLjg5LDI2Ny43OSw3NzIuNzksMjQ4LjQ5LDc1Ni4yNywyMzUuNTZaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNODA1Ljc4LDI4Ny41NmMtMy4yNS00MS40LTM0LjIxLTc2Ljg4LTc1LjM2LTg2LjJhOTMuMzQsOTMuMzQsMCwwLDAtMjQuMTctMi4yOGMtMjIuMi01Mi4xNS01OC43OS0xMDAuNTQtMTAxLjM2LTEzMy42NEM1MzAuODEsNy43Miw0NDYuNDgtLjQ0LDQwMy4xLDBjLTQzLjg0LS40MS0xMjcuOTEsNy43LTIwMiw2NS40MkMxNTguNTgsOTguNTgsMTIyLDE0Nyw5OS43NSwxOTkuMDhhOTMuNTEsOTMuNTEsMCwwLDAtMjQuMTcsMi4yOGMtNDEuMTUsOS4zMi03Mi4xMSw0NC44LTc1LjMxLDg2LjJzMjIuMDUsODEuMjMsNjEuMzcsOTYuNjlhOTUuNDksOTUuNDksMCwwLDAsMjUuNTksNi4xOGMyLjI4LDYuOTUsNC43MiwxMy43OSw3LjU1LDIwLjM3QzE2Mi4wOCw1NjcuMjksMzYzLjM3LDU3NS4yNSw0MDMsNTc1LjI1czI0MC45Mi04LDMwOC4yNy0xNjQuNDVjMi44My02LjYzLDUuMjctMTMuNDgsNy41NS0yMC4zN2E5NS4xNCw5NS4xNCwwLDAsMCwyNS41NC02LjE4Qzc4My43MywzNjguNzksODA5LDMyOSw4MDUuNzgsMjg3LjU2Wk00MS4yNywzNDYuNDljLTE0LjQ0LTE1LjI1LTIyLjQ1LTM2LTIwLjc4LTU3LjM2czEyLjcyLTQwLjY0LDI5LjI5LTUzLjU3YTE0Ni4zNCwxNDYuMzQsMCwwLDAtOC41MSwxMTAuOTNabTQwLDIyLjU2YTgwLjI2LDgwLjI2LDAsMCwxLTkuMTItMi41OSwxMjkuNjUsMTI5LjY1LDAsMCwxLTE3LjczLTY3LjljLjg2LTM4LjExLDE5LTY1Ljc4LDI4Ljg4LTc4LjA1YTgyLjc3LDgyLjc3LDAsMCwxLDguNTItMS4xMUM3NC4wNiwyNzAuMjgsNzAuNDYsMzIxLjQ2LDgxLjMsMzY5LjA1Wm02MjAuMjQsOC44NmEyMTYuNTksMjE2LjU5LDAsMCwxLTguODcsMjQuODlDNjMwLjM0LDU0Ny42Myw0NDAuNDUsNTU1LDQwMyw1NTVTMTc1LjcyLDU0Ny42MywxMTMuMzgsNDAyLjhhMjE4LjcyLDIxOC43MiwwLDAsMS04LjkyLTI0Ljg5di0uMDVjLTE1LjEtNTEuMjMtMTEuMjUtMTA4LDExLjE1LTE2NC4xNCwyMC41My01MS41OSw1Ni4yNS05OS44Myw5OC0xMzIuMzIsNjcuOTEtNTIuODUsMTQ1LjI5LTYxLjExLDE4Ny4xLTYxLjExaDIuNDNjNDAuOTUtLjQxLDEyMC4xLDcuMTksMTg5LjMzLDYxLjE3LDQxLjc2LDMyLjQzLDc3LjQzLDgwLjY3LDk4LDEzMi4yNkM3MTIuODQsMjY5Ljg3LDcxNi42OSwzMjYuNjMsNzAxLjU0LDM3Ny45MVptMzIuMzMtMTEuNDVhODAuMjYsODAuMjYsMCwwLDEtOS4xMiwyLjU5YzEwLjg0LTQ3LjU5LDcuMjQtOTguNzctMTAuNi0xNDkuNjVhODIsODIsMCwwLDEsOC41NywxLjExYzExLjMsMTQsMjgsNDAuOTUsMjguODQsNzguMUExMjkuNzMsMTI5LjczLDAsMCwxLDczMy44NywzNjYuNDZabTMxLjA2LTIwLjEyYTE0Ny43MywxNDcuNzMsMCwwLDAtOC42Ni0xMTAuNzhjMTYuNTIsMTIuOTMsMjcuNjIsMzIuMjMsMjkuMjksNTMuNTdBNzUuNDcsNzUuNDcsMCwwLDEsNzY0LjkzLDM0Ni4zNFoiLz48cGF0aCBjbGFzcz0iY2xzLTEiIGQ9Ik03MTQuMTUsMjE5LjRjMTcuODQsNTAuODgsMjEuNDQsMTAyLjA2LDEwLjYsMTQ5LjY1YTgwLjI2LDgwLjI2LDAsMCwwLDkuMTItMi41OSwxMjkuNzMsMTI5LjczLDAsMCwwLDE3LjY5LTY3Ljg1Yy0uODItMzcuMTUtMTcuNTQtNjQuMDYtMjguODQtNzguMUE4Miw4MiwwLDAsMCw3MTQuMTUsMjE5LjRaIi8+PHBhdGggY2xhc3M9ImNscy0zIiBkPSJNNDAzLDk0LjQzYy0yMy43MiwwLTExNC42OCwwLTE3OC45NCw2OS43My02My41NSw2OC45Mi04Mi42NSwxODYuMTQtMjYuNCwyNjcuNzgsNTkuMjksODYsMTcwLjE3LDg2LDIwNS4zNCw4NnMxNDYsMCwyMDUuMzQtODZjNTYuMy04MS42NCwzNy4xNC0xOTguODYtMjYuMzUtMjY3Ljc4QzUxNy43Myw5NC40Myw0MjYuNzcsOTQuNDMsNDAzLDk0LjQzWk0yNjIuODgsMzUzLjE5Yy03LjI1LDEzLjA3LTI0LjUzLDE4Ljk1LTM4LjMxLDExLjgtMTQuNzUtNy42LTE5LjMxLTI2LjktMTEuMjUtNDAuNTQsNi45NC0xMS43NiwyMi4xOS0xNy4zMywzNC45MS0xMi4zNkMyNjMuNzksMzE4LjEyLDI3MS4xOSwzMzguMTgsMjYyLjg4LDM1My4xOVpNMzc2LjcsMjAxYy0xMS40LDIwLjc4LTM5LjQzLDguNzctNjMuOSwzMi41NEMyOTAsMjU1LjY4LDI5OSwyODEuMTIsMjc5LDI4OS44OWMtMTUuNjYsNi44NC0zOC4yNy0xLjMyLTQ4LjY2LTE1LjMtMTkuMTUtMjUuOCwzLjMtNzAuOTUsMjQuNDMtOTQuNTEsMjcuNjItMzAuODcsODAuODMtNTUuNzUsMTA4LjE5LTM0LjU3QzM3OC40MiwxNTcuNDcsMzg1Ljg3LDE4NC4zMywzNzYuNywyMDFaIi8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNNTkyLjQzLDgxLjQ2Yy02OS4yMy01NC0xNDguMzgtNjEuNTctMTg5LjMzLTYxLjE3aC0yLjQzYy00MS44MSwwLTExOS4xOSw4LjI2LTE4Ny4xLDYxLjEyLTQxLjcxLDMyLjQ4LTc3LjQzLDgwLjczLTk4LDEzMi4zMi0yMi40LDU2LjE0LTI2LjI1LDExMi45LTExLjE1LDE2NC4xNHYuMDVhMjE5LjI1LDIxOS4yNSwwLDAsMCw4LjkyLDI0Ljg4QzE3NS43Miw1NDcuNjMsMzY1LjYsNTU1LDQwMyw1NTVzMjI3LjM0LTcuMzUsMjg5LjY3LTE1Mi4xOGEyMTksMjE5LDAsMCwwLDguODctMjQuODhjMTUuMTUtNTEuMjksMTEuMy0xMDgtMTEuMS0xNjQuMTlDNjY5Ljg2LDE2Mi4xNCw2MzQuMTksMTEzLjg5LDU5Mi40Myw4MS40NlptMTUuOTEsMzUwLjQ4Yy01OS4yOSw4Ni0xNzAuMTIsODYtMjA1LjM0LDg2cy0xNDYsMC0yMDUuMzQtODZjLTU2LjI1LTgxLjY0LTM3LjE1LTE5OC44NiwyNi40LTI2Ny43OEMyODguMzIsOTQuNDMsMzc5LjI5LDk0LjQzLDQwMyw5NC40M3MxMTQuNzQsMCwxNzksNjkuNzNDNjQ1LjQ5LDIzMy4wOCw2NjQuNjQsMzUwLjMsNjA4LjM0LDQzMS45NFoiLz48cGF0aCBjbGFzcz0iY2xzLTEiIGQ9Ik0zNjMsMTQ1LjUxYy0yNy4zNi0yMS4xOC04MC41NywzLjctMTA4LjE5LDM0LjU2LTIxLjEzLDIzLjU3LTQzLjU4LDY4LjcyLTI0LjQzLDk0LjUxLDEwLjM5LDE0LDMzLDIyLjE1LDQ4LjY2LDE1LjMxLDIwLTguNzcsMTEtMzQuMjEsMzMuOC01Ni40MSwyNC40Ny0yMy43Niw1Mi41LTExLjc1LDYzLjktMzIuNTNDMzg1Ljg3LDE4NC4zMywzNzguNDIsMTU3LjQ3LDM2MywxNDUuNTFaIi8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNMjQ4LjIzLDMxMi4wOWMtMTIuNzItNS0yOCwuNjEtMzQuOTEsMTIuMzYtOC4wNiwxMy42NC0zLjUsMzIuOTQsMTEuMjUsNDAuNTQsMTMuNzgsNy4xNSwzMS4wNiwxLjI3LDM4LjMxLTExLjhDMjcxLjE5LDMzOC4xOCwyNjMuNzksMzE4LjEyLDI0OC4yMywzMTIuMDlaIi8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNNTQuNDUsMjk4LjU2YTEyOS42NSwxMjkuNjUsMCwwLDAsMTcuNzMsNjcuOSw4MC4yNiw4MC4yNiwwLDAsMCw5LjEyLDIuNTljLTEwLjg0LTQ3LjU5LTcuMjQtOTguNzcsMTAuNTQtMTQ5LjY1YTgyLjk0LDgyLjk0LDAsMCwwLTguNTEsMS4xMUM3My41LDIzMi43OCw1NS4zMSwyNjAuNDUsNTQuNDUsMjk4LjU2WiIvPjxwYXRoIGNsYXNzPSJjbHMtMSIgZD0iTTIwLjQ5LDI4OS4xM2MtMS42NywyMS4zOSw2LjM0LDQyLjExLDIwLjc4LDU3LjM2YTE0Ni4yMSwxNDYuMjEsMCwwLDEsOC41MS0xMTAuOTNDMzMuMjEsMjQ4LjQ5LDIyLjExLDI2Ny43OSwyMC40OSwyODkuMTNaIi8+PC9nPjwvZz48L3N2Zz4=',
    //   url: "https://spacebudz.io",
    //   tags: ["tag1", "tag2"],
    //   registrationTime: "2021-01-01T00:00:00Z",
    //   verified: true,
    // },
    // {
    //   id: 3,
    //   name: "HAZELpool",
    //   category: ProjectCategory.SPO,
    //   logo: 'https://static.wixstatic.com/media/671042_f94cc183082a4f82af0467b8178a812a~mv2.png/v1/fill/w_74,h_80,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/hazelkittens-logo-vector_topleft.png',
    //   url: "https://hazelpool.com",
    //   tags: ["tag1", "tag2"],
    //   registrationTime: "2021-01-01T00:00:00Z",
    //   verified: true,
    // },
    // {
    //   id: 4,
    //   name: "HOSKY",
    //   category: ProjectCategory.dRep,
    //   logo: 'https://hosky.io/wp-content/uploads/2022/05/Front-300x300.png',
    //   url: "https://hosky.io",
    //   tags: ["tag1", "tag2"],
    //   registrationTime: "2021-01-01T00:00:00Z",
    //   verified: false,
    // },
    // {
    //   id: 5,
    //   name: "Summon Platform",
    //   category: ProjectCategory.DAO,
    //   logo: 'https://summonplatform.io/wp-content/uploads/2022/07/Summon-Token-1024x1024.png',
    //   url: "https://summonplatform.io",
    //   tags: ["tag1", "tag2"],
    //   registrationTime: "2021-01-01T00:00:00Z",
    //   verified: false,
    // },
    // {
    //   id: 6,
    //   name: "ADA Handle",
    //   category: ProjectCategory.Other,
    //   logo: 'https://miro.medium.com/v2/resize:fit:1400/1*zvssHccJdcc4YhJe_YtNsw.jpeg',
    //   url: "https://handle.me",
    //   tags: ["tag1", "tag2"],
    //   registrationTime: "2021-01-01T00:00:00Z",
    //   verified: false,
    // }

    // ]
    return (await axios.get(`${this.baseUrl}/projects`)).data;
  }

  async getSubscriptions(): Promise<Subscription[]> {
    await getRandomDelay();
    return [{
      projectId: 1,
      defaultStatus: DefaultSubscriptionStatus.Subscribed,
      currentStatus: SubscriptionStatus.Default,
      favorite: true,
    }, {
      projectId: 2,
      defaultStatus: DefaultSubscriptionStatus.Unsubscribed,
      currentStatus: SubscriptionStatus.Default,
    }, {
      projectId: 3,
      defaultStatus: DefaultSubscriptionStatus.Unsubscribed,
      currentStatus: SubscriptionStatus.Subscribed,
      favorite: true,
    }, {
      projectId: 4,
      defaultStatus: DefaultSubscriptionStatus.Unsubscribed,
      currentStatus: SubscriptionStatus.Unsubscribed,
    }, {
      projectId: 5,
      defaultStatus: DefaultSubscriptionStatus.Subscribed,
      currentStatus: SubscriptionStatus.Muted,
    }]
    // return (await axios.get(`${this.baseUrl}/subscriptions`)).data;
  }

}