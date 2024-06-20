import {  MdDangerous, MdVerified } from 'react-icons/md';
import { Tag, TagLabel, TagLeftIcon } from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';

export default function VerifiedTag({ isVerified }: { isVerified: boolean }) {
  const { t } = useTranslation('projects');
  let colorScheme;
  let icon;
  let tagText;

  if (isVerified) {
    icon = MdVerified;
    colorScheme = 'green';
    tagText = t('verified');
  } else {
    icon = MdDangerous;
    colorScheme = 'red';
    tagText = t('notVerified');
  }

  return (
    <Tag size="md" variant="solid" colorScheme={colorScheme}>
      <TagLeftIcon boxSize="12px" as={icon} />
      <TagLabel>{tagText}</TagLabel>
    </Tag>
  );
};
