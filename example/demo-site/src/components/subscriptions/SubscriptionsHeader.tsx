import {
  Box, Button, Container, HStack, Icon, IconButton, Input, InputGroup, InputLeftElement, InputRightAddon, InputRightElement, Text, useColorModeValue as mode,
} from '@chakra-ui/react';
import { FaArrowLeft, FaListUl, FaThLarge } from 'react-icons/fa';
import {
  FiDelete,
  FiSearch,
} from 'react-icons/fi';
import { RadioIconButton, RadioIconButtonGroup } from '../RadioIconButtonGroup';
import ProjectViewType from '../../lib/types/ProjectViewType';
import CheckboxFilterPopover, { FilterData } from '../filters/CheckboxFilterPopover';
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';
import { MdClear } from 'react-icons/md';

export type SubscriptionsHeaderProps = {
  title: string;
  currentType: ProjectViewType;
  onChangeType(val: ProjectViewType): void;
  onSearch(val: string): void;
  searchTerm?: string;
  filterData: FilterData;
  onChangeFilter(value: string[]): void;
};

export default function SubscriptionHeader(props: SubscriptionsHeaderProps) {
  const {
    title, currentType, onChangeType, searchTerm, onSearch, filterData, onChangeFilter,
  } = props;
  const { t } = useTranslation('projects');
  const { t: tc } = useTranslation('common');
  return (<Box as="section" pt={{ base: '4', md: '8' }} pb={{ base: '12', md: '12' }}>
      <Container px={0}>
        <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
          <HStack spacing="4" justify="space-between">
            <NextLink href="/projects">
              <Button variant="ghost" size="lg">
                <HStack><FaArrowLeft /><Text>{t('backToCategories')}</Text></HStack>
              </Button>
            </NextLink>
            <Text textStyle="lg" fontWeight="medium" flexGrow="1">
              {title}
            </Text>
            <InputGroup maxW="2xs" display={{ base: 'none', md: 'inline-flex' }}>
              <InputLeftElement>
                <Icon as={FiSearch} color={mode('fg.accent', 'fg.accent.muted')} fontSize="lg" />
              </InputLeftElement>
              <Input placeholder={t('searchProjectsPlaceholder')} variant={mode('flushed', 'flushed')} value={searchTerm} onChange={(event) => onSearch(event.target.value)} />
              {searchTerm?.length && (<InputRightElement>
                <IconButton as={MdClear} color={mode('fg.accent', 'fg.accent.muted')} size="xs" variant="ghost" onClick={() => onSearch('')} aria-label={tc('clearSearch')} />
              </InputRightElement>)}
            </InputGroup>
            <CheckboxFilterPopover filterData={filterData} onSubmit={onChangeFilter} />
            <RadioIconButtonGroup defaultValue={ProjectViewType.card} value={currentType} size='md' onChange={(val: ProjectViewType) => onChangeType(val)}>
              <RadioIconButton value="card" aria-label={t('tileView.name')} icon={<FaThLarge />} />
              <RadioIconButton value="list" aria-label={t('listView.name')} icon={<FaListUl />} />
            </RadioIconButtonGroup>
          </HStack>
        </Box>
      </Container>
    </Box>);
};
