import {
  FaStar,
} from 'react-icons/fa';
import { MdApps, MdAssuredWorkload, MdBusiness, MdCurrencyExchange, MdOutlineError, MdWaterDrop } from 'react-icons/md';
import { Tag, TagLabel, TagLeftIcon } from '@chakra-ui/react';
import ProjectCategory from '@/lib/types/ProjectCategory';

export default function ProjectTag({ category }: { category: ProjectCategory }) {
  let colorScheme;
  let icon;
  let tagText;

  switch (category) {
    case ProjectCategory.DeFi:
      icon = MdCurrencyExchange;
      colorScheme = 'green';
      tagText = 'DeFi';
      break;
    case ProjectCategory.NFT:
      icon = FaStar;
      colorScheme = 'yellow';
      tagText = 'NFT';
      break;
    case ProjectCategory.SPO:
      icon = MdWaterDrop;
      colorScheme = 'blue';
      tagText = 'Stakepool';
      break;
    case ProjectCategory.dRep:
      icon = MdAssuredWorkload;
      colorScheme = 'purple';
      tagText = 'dRep';
      break;
    case ProjectCategory.DAO:
      icon = MdBusiness;
      colorScheme = 'orange';
      tagText = 'Decentralized Organization';
      break;
    case ProjectCategory.Other:
      icon = MdApps;
      colorScheme = 'red';
      tagText = 'Other';
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
