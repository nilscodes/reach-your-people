import {
  Avatar,
  Button,
  Menu,
  MenuButton,
  MenuDivider,
  MenuItem,
  MenuItemOption,
  MenuList,
  MenuOptionGroup,
  Text,
  useBreakpointValue,
  useColorMode,
  useColorModeValue,
} from '@chakra-ui/react'
import type { User } from 'next-auth';
import { signOut } from "next-auth/react";
import useTranslation from 'next-translate/useTranslation';

type ProfileMenuProps = {
  user: Omit<User, 'id'>;
}

export const ProfileMenu = ({ user }: ProfileMenuProps) => {
  const { t } = useTranslation('common');
  const showColorModeSwitcherInMenu = useBreakpointValue(
    {
      base: 'true',
      md: 'false',
    },
    {
      fallback: 'true',
    },
  ) === 'true';
  const { toggleColorMode } = useColorMode()
  const colorModeText = useColorModeValue("switchToDarkMode", "switchToLightMode");
  const userDisplayName = user.name ?? (user.email ?? 'Unknown');
  return (
    <Menu>
      <MenuButton as={Button} variant='link'>
        <Avatar boxSize="10" src={user.image === null ? undefined : user.image} bgColor='white' name={userDisplayName} />
      </MenuButton>
      <MenuList shadow="lg" py="4" color={useColorModeValue('gray.600', 'gray.200')} px="3">
        <Text fontWeight="medium" mb="2">
          {userDisplayName}
        </Text>
        {/* <MenuOptionGroup defaultValue="chakra-ui">
          <MenuItemOption value="chakra-ui" fontWeight="semibold" rounded="md">
            Chakra UI
          </MenuItemOption>
          <MenuItemOption value="careerlyft" fontWeight="semibold" rounded="md">
            CareerLyft
          </MenuItemOption>
        </MenuOptionGroup> */}
        {/* <MenuDivider />
        <MenuItem rounded="md">Workspace settings</MenuItem>
        <MenuItem rounded="md">Add an account</MenuItem>
        <MenuDivider /> */}
        {showColorModeSwitcherInMenu && (<>
          <MenuItem rounded="md" onClick={toggleColorMode}>{t(colorModeText)}</MenuItem>
        </>)}
        <MenuItem rounded="md" onClick={() => signOut({ callbackUrl: "/" })}>{t('signOut')}</MenuItem>
      </MenuList>
    </Menu>
  )
}