import {
  Box, Button, Container, HStack, Icon, IconButton, Input, InputGroup, InputLeftElement, InputRightElement, Stack, Text, useColorModeValue as mode,
} from '@chakra-ui/react';
import { FaArrowLeft, FaListUl, FaThLarge } from 'react-icons/fa';
import { FiSearch } from 'react-icons/fi';
import { RadioIconButton, RadioIconButtonGroup } from '../RadioIconButtonGroup';
import ProjectViewType from '../../lib/types/ProjectViewType';
import CheckboxFilterPopover, { FilterData } from '../filters/CheckboxFilterPopover';
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';
import { MdClear } from 'react-icons/md';
import { VerifiedFilterPopover } from '../filters/VerifiedFilterPopover';
import { SubscriptionsHeaderProps } from './types';

export default function SubscriptionsHeader(props: SubscriptionsHeaderProps) {
  const {
    title, currentType, onChangeType, searchTerm, onSearch, filterData, verifiedFilterValue, onChangeFilter, onChangeVerifiedFilter
  } = props;
  const { t } = useTranslation('projects');
  const { t: tc } = useTranslation('common');
  return (<Box as="section" pt={{ base: '4', md: '8' }} pb={{ base: '12', md: '12' }}>
      <Container px={0}>
        <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
          <Stack spacing="4" justify="space-between" direction={{ base: 'column', md: 'row' }}>
            <HStack spacing="4">
              <NextLink href="/projects">
                <Button variant="ghost" size="lg">
                  <HStack><FaArrowLeft /><Text>{t('backToCategories')}</Text></HStack>
                </Button>
              </NextLink>
              <Text textStyle="lg" fontWeight="medium" flexGrow="1">
                {title}
              </Text>
            </HStack>
            <Stack direction={{ base: 'column', md: 'row' }} spacing="4">
              <InputGroup maxW="2xs">
                <InputLeftElement>
                  <Icon as={FiSearch} color={mode('fg.accent', 'fg.accent.muted')} fontSize="lg" />
                </InputLeftElement>
                <Input placeholder={t('searchProjectsPlaceholder')} variant={mode('flushed', 'flushed')} value={searchTerm} onChange={(event) => onSearch(event.target.value)} />
                {searchTerm?.length && (<InputRightElement>
                  <IconButton as={MdClear} color={mode('fg.accent', 'fg.accent.muted')} size="xs" variant="ghost" onClick={() => onSearch('')} aria-label={tc('clearSearch')} />
                </InputRightElement>)}
              </InputGroup>
              <CheckboxFilterPopover filterData={filterData} onSubmit={onChangeFilter} />
              <Stack spacing="4" direction={{ base: 'column', md: 'row' }}>
                <VerifiedFilterPopover defaultValue={verifiedFilterValue} onSubmit={onChangeVerifiedFilter} />
                <RadioIconButtonGroup defaultValue={ProjectViewType.card} value={currentType} size='md' onChange={(val: ProjectViewType) => onChangeType(val)}>
                  <RadioIconButton value="card" aria-label={t('tileView.name')} icon={<FaThLarge />} />
                  <RadioIconButton value="list" aria-label={t('listView.name')} icon={<FaListUl />} />
                </RadioIconButtonGroup>
              </Stack>
            </Stack>
          </Stack>
        </Box>
      </Container>
    </Box>);
};
