import {
    Container, Text
} from '@chakra-ui/react';
import { MdBusiness, MdCelebration, MdCheck, MdCurrencyExchange, MdPerson, MdWaterDrop } from 'react-icons/md';
import { RadioCard, RadioCardGroup } from '../RadioCardGroup';
import ProjectCategory, { ProjectCategoryNames } from '@/lib/types/ProjectCategory';
import useTranslation from 'next-translate/useTranslation';

export const projectTypeOptions = [{
    value: ProjectCategory.NFT,
    variant: 'outline',
    icon: <MdCheck />,
    disabled: false,
}, {
    value: ProjectCategory.DeFi,
    variant: 'outline',
    icon: <MdCurrencyExchange />,
    disabled: false,
}, {
    value: ProjectCategory.DAO,
    variant: 'outline',
    icon: <MdBusiness />,
    disabled: false,
}, {
    value: ProjectCategory.Other,
    variant: 'outline',
    icon: <MdCelebration />,
    disabled: false,
}, {
    value: ProjectCategory.SPO,
    variant: 'outline',
    icon: <MdWaterDrop />,
    disabled: false,
}, {
    value: ProjectCategory.dRep,
    variant: 'link',
    icon: <MdPerson />,
    disabled: true,
}];

type ProjectTypeSelectionProps = {
    handleChange: (value: ProjectCategory) => void;
    type: ProjectCategory | null;
}


export default function ProjectTypeSelection({ handleChange, type }: ProjectTypeSelectionProps) {
    const { t } = useTranslation('common');
    return (<Container maxW="3xl">
        <RadioCardGroup spacing="3" onChange={handleChange} defaultValue={type ?? undefined}>
            {projectTypeOptions.filter((option) => type === null || option.value === type).map((option) => (
                <RadioCard key={option.value} value={option.value} icon={option.icon} iconVariant={option.variant} radioProps={{ isDisabled: option.disabled }}>
                    <Text color="fg.emphasized" fontWeight="bold" fontSize="sm">
                        {t(`categories.${ProjectCategoryNames[option.value]}.name`)}
                    </Text>
                    <Text color="fg.muted" fontSize="sm">
                        {t(`categories.${ProjectCategoryNames[option.value]}.createInfo`)}
                    </Text>
                </RadioCard>
            ))}
        </RadioCardGroup>
    </Container>);
}