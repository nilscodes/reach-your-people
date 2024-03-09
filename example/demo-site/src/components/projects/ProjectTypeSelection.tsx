import {
    Container, Text
} from '@chakra-ui/react';
import { Md10K, MdBusiness, MdCelebration, MdCheck, MdCurrencyExchange, MdNotInterested, MdPartyMode, MdPerson, MdVolumeMute, MdWaterDrop } from 'react-icons/md';
import { RadioCard, RadioCardGroup } from '../RadioCardGroup';
import ProjectCategory, { ProjectCategoryNames } from '@/lib/types/ProjectCategory';

export const projectTypeOptions = [{
    value: ProjectCategory.NFT,
    label: ProjectCategoryNames[ProjectCategory.NFT],
    description: 'Create and sell digital assets',
    variant: 'outline',
    icon: <MdCheck />,
}, {
    value: ProjectCategory.DeFi,
    label: ProjectCategoryNames[ProjectCategory.DeFi],
    description: 'Decentralized finance',
    variant: 'outline',
    icon: <MdCurrencyExchange />,
}, {
    value: ProjectCategory.SPO,
    label: ProjectCategoryNames[ProjectCategory.SPO],
    description: 'Stake pool operator',
    variant: 'outline',
    icon: <MdWaterDrop />,
}, {
    value: ProjectCategory.dRep,
    label: ProjectCategoryNames[ProjectCategory.dRep],
    description: 'Delegated representative',
    variant: 'outline',
    icon: <MdPerson />,
}, {
    value: ProjectCategory.DAO,
    label: ProjectCategoryNames[ProjectCategory.DAO],
    description: 'Decentralized autonomous organization',
    variant: 'outline',
    icon: <MdBusiness />,
}, {
    value: ProjectCategory.Other,
    label: ProjectCategoryNames[ProjectCategory.Other],
    description: 'Other type of project',
    variant: 'outline',
    icon: <MdCelebration />,
}];

type ProjectTypeSelectionProps = {
    handleChange: (value: ProjectCategory) => void;
    type: ProjectCategory | null;
}


export default function ProjectTypeSelection({ handleChange, type }: ProjectTypeSelectionProps) {
    return (<Container maxW="3xl">
        <RadioCardGroup spacing="3" onChange={handleChange} defaultValue={type ?? undefined}>
            {projectTypeOptions.filter((option) => type === null || option.value === type).map((option) => (
                <RadioCard key={option.value} value={option.value} icon={option.icon} iconVariant={option.variant}>
                    <Text color="fg.emphasized" fontWeight="bold" fontSize="sm">
                        {option.label}
                    </Text>
                    <Text color="fg.muted" fontSize="sm">
                        {option.description}
                    </Text>
                </RadioCard>
            ))}
        </RadioCardGroup>
    </Container>);
}