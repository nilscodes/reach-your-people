import { Account } from "../../lib/ryp-subscription-api";
import NFTConfiguration from "./NFTConfiguration";
import ProjectCategory from "@/lib/types/ProjectCategory";
import { ComponentType } from "react";
import { ProjectData } from "./NewProject";
import StakepoolConfiguration from "./StakepoolConfiguration";

export type ProjectConfigurationProps = {
    account: Account;
    type: ProjectCategory;
    formData: ProjectData;
    onSubmit: (formData: ProjectData) => void;
};

const configurationsMap: Record<ProjectCategory, ComponentType<ProjectConfigurationProps>> = {
    [ProjectCategory.DeFi]: NFTConfiguration,
    [ProjectCategory.NFT]: NFTConfiguration,
    [ProjectCategory.SPO]: StakepoolConfiguration,
    [ProjectCategory.dRep]: NFTConfiguration,
    [ProjectCategory.DAO]: NFTConfiguration,
    [ProjectCategory.Other]: NFTConfiguration,
};

export default function ProjectConfiguration({ account, type, formData, onSubmit }: ProjectConfigurationProps) {
    const Configuration = configurationsMap[type];
    return (
        <Configuration account={account} type={type} formData={formData} onSubmit={onSubmit} />
    );
}
