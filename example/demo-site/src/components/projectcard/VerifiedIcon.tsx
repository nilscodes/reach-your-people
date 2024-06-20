import {
    Icon,
    IconProps,
    useColorModeValue,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import { MdDangerous, MdVerified } from 'react-icons/md';

interface VerifiedIconProps extends IconProps {
    isVerified: boolean;
};


export default function VerifiedIcon({ isVerified, fontSize }: VerifiedIconProps) {
    const { t } = useTranslation('projects');
    const verifiedIcon = isVerified ? MdVerified : MdDangerous;
    const iconBaseColor = isVerified ? 'green' : 'red';
    const iconColor = useColorModeValue(`${iconBaseColor}.600`, `${iconBaseColor}.400`);
    const title = isVerified ? t('verified') : t('notVerified');
    return (<Icon as={verifiedIcon} fontSize={fontSize} color={iconColor} title={title} />)
};
