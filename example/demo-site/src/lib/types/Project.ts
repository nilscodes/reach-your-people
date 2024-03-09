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