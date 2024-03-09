import {
  Avatar, AvatarBadge, AvatarProps, useColorModeValue,
} from '@chakra-ui/react';
import VerifiedIcon from './VerifiedIcon';

interface ProjectLogoProps extends AvatarProps {
  isVerified?: boolean
  hideVerified?: boolean
}

export default function ProjectLogo(props: ProjectLogoProps) {
  const { isVerified, hideVerified, ...avatarProps } = props;
  const avatarColor = useColorModeValue('white', 'gray.700');

  return (
    <Avatar size="2xl" {...avatarProps} scale={0.5}>
      {!hideVerified && (
        <AvatarBadge
          borderWidth="4px"
          borderColor={avatarColor}
          insetEnd="3"
          bottom="3"
          bg={avatarColor}
        >
          <VerifiedIcon isVerified={isVerified ?? false} fontSize="3xl" />
        </AvatarBadge>
      )}
    </Avatar>
  );
};
