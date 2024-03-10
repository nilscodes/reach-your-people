import { Account } from "../../lib/ryp-subscription-api";
import NFTConfiguration from "./NFTConfiguration";
import ProjectCategory from "@/lib/types/ProjectCategory";
import { ComponentType } from "react";
import { FormData } from "./NewProject";

export type ProjectConfigurationProps = {
    account: Account;
    type: ProjectCategory;
    formData: FormData;
    onFormChange: (field: keyof FormData, value: string) => void;
    onSubmit: () => void;
};

const configurationsMap: Record<ProjectCategory, ComponentType<ProjectConfigurationProps>> = {
    [ProjectCategory.DeFi]: NFTConfiguration,
    [ProjectCategory.NFT]: NFTConfiguration,
    [ProjectCategory.SPO]: NFTConfiguration,
    [ProjectCategory.dRep]: NFTConfiguration,
    [ProjectCategory.DAO]: NFTConfiguration,
    [ProjectCategory.Other]: NFTConfiguration,
};

export default function ProjectConfiguration({ account, type, formData, onFormChange, onSubmit }: ProjectConfigurationProps) {
    const Configuration = configurationsMap[type];
    return (
        <Configuration account={account} type={type} formData={formData} onFormChange={onFormChange} onSubmit={onSubmit} />
    );
}
