import {
    Icon,
    IconProps,
    useColorModeValue,
} from '@chakra-ui/react';
import { MdDangerous, MdVerified } from 'react-icons/md';

interface VerifiedIconProps extends IconProps {
    isVerified: boolean;
};


export default function VerifiedIcon({ isVerified, fontSize }: VerifiedIconProps) {
    const verifiedIcon = isVerified ? MdVerified : MdDangerous;
    const iconBaseColor = isVerified ? 'green' : 'red';
    const iconColor = useColorModeValue(`${iconBaseColor}.600`, `${iconBaseColor}.400`);
    const title = isVerified ? 'Is Verified' : 'Not Verified';
    return (<Icon as={verifiedIcon} fontSize={fontSize} color={iconColor} title={title} />)
};
