import ProjectCategory from "./ProjectCategory";

export type Project = {
    id: number;
    name: string;
    category: ProjectCategory;
    logo: string;
    url: string;
    tags?: string[];
    registrationTime: string;
    verified: boolean;
}

// Request is Project without registrationTime, verified and id
export type ProjectCreationRequest = Omit<Project, 'registrationTime' | 'verified' | 'id' | 'logo'>;