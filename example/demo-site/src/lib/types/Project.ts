import ProjectCategory from "./ProjectCategory";

export type Policy = {
    name: string;
    policyId: string;
    manuallyVerified?: string;
}

export type Stakepool = {
    poolHash: string;
    verificationNonce: string;
}

export type Project = {
    id: number;
    name: string;
    description: string;
    category: ProjectCategory;
    logo: string;
    url: string;
    tags?: string[];
    registrationTime: string;
    manuallyVerified?: string;
    policies: Policy[];
    stakepools: Stakepool[];
}

// Request is Project without registrationTime, verified and id
export type ProjectCreationRequest = Omit<Project, 'registrationTime' | 'verified' | 'id' | 'logo'>;