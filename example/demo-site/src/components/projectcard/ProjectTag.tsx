import {
  FaStar,
} from 'react-icons/fa';
import { MdApps, MdAssuredWorkload, MdBusiness, MdCurrencyExchange, MdOutlineError, MdWaterDrop } from 'react-icons/md';
import { Tag, TagLabel, TagLeftIcon } from '@chakra-ui/react';
import ProjectCategory from '@/lib/types/ProjectCategory';
import useTranslation from 'next-translate/useTranslation';

export default function ProjectTag({ category }: { category: ProjectCategory }) {
  const { t } = useTranslation('common');
  let colorScheme;
  let icon;
  let tagText;

  switch (category) {
    case ProjectCategory.DeFi:
      icon = MdCurrencyExchange;
      colorScheme = 'green';
      tagText = t('categories.defi.tag');
      break;
    case ProjectCategory.NFT:
      icon = FaStar;
      colorScheme = 'yellow';
      tagText = t('categories.nft.tag');
      break;
    case ProjectCategory.SPO:
      icon = MdWaterDrop;
      colorScheme = 'blue';
      tagText = t('categories.spo.tag');
      break;
    case ProjectCategory.dRep:
      icon = MdAssuredWorkload;
      colorScheme = 'purple';
      tagText = t('categories.drep.tag');
      break;
    case ProjectCategory.DAO:
      icon = MdBusiness;
      colorScheme = 'orange';
      tagText = t('categories.dao.tag');
      break;
    case ProjectCategory.Other:
      icon = MdApps;
      colorScheme = 'red';
      tagText = t('categories.other.tag');
      break;
    default:
      // Default case, if the category is not recognized
      colorScheme = 'gray';
      icon = MdOutlineError;
      tagText = 'N/A';
      break;
  }

  return (
    <Tag size="md" variant="solid" colorScheme={colorScheme}>
      <TagLeftIcon boxSize="12px" as={icon} />
      <TagLabel>{tagText}</TagLabel>
    </Tag>
  );
};
