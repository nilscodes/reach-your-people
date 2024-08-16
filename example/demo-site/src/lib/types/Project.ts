import ProjectCategory from "./ProjectCategory";

export type Policy = {
    name: string;
    policyId: string;
    manuallyVerified?: string;
}

export type Stakepool = {
    poolHash: string;
    verificationNonce: string;
    verificationTime: string;
}

export type DRep = {
    drepId: string;
    verificationNonce: string;
    verificationTime: string;
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
    dreps: DRep[];
}

// Request is Project without registrationTime, verified and id
export type ProjectCreationRequest = Omit<Project, 'registrationTime' | 'verified' | 'id' | 'logo'>;