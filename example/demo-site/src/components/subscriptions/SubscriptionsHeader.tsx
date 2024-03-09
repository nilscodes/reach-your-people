import {
  Box, Button, Container, HStack, Icon, Input, InputGroup, InputLeftElement, Text, useColorModeValue as mode,
} from '@chakra-ui/react';
import { FaArrowLeft, FaListUl, FaThLarge } from 'react-icons/fa';
import {
  FiSearch,
} from 'react-icons/fi';
import { RadioIconButton, RadioIconButtonGroup } from '../RadioIconButtonGroup';
import ProjectViewType from '../../lib/types/ProjectViewType';
import CheckboxFilterPopover, { FilterData } from '../filters/CheckboxFilterPopover';
import NextLink from '../NextLink';

export type SubscriptionsHeaderProps = {
  title: string;
  currentType: ProjectViewType;
  onChangeType(val: ProjectViewType): void;
  onSearch(val: string): void;
  filterData: FilterData;
  onChangeFilter(value: string[]): void;
};

export default function SubscriptionHeader(props: SubscriptionsHeaderProps) {
  const {
    title, currentType, onChangeType, onSearch, filterData, onChangeFilter,
  } = props;
  return (<Box as="section" pt={{ base: '4', md: '8' }} pb={{ base: '12', md: '12' }}>
      <Container px={0}>
        <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
          <HStack spacing="4" justify="space-between">
            <NextLink href="/subscriptions">
              <Button variant="ghost" size="lg">
                <HStack><FaArrowLeft /><Text>Categories</Text></HStack>
              </Button>
            </NextLink>
            <Text textStyle="lg" fontWeight="medium" flexGrow="1">
              {title}
            </Text>
            <InputGroup maxW="2xs" display={{ base: 'none', md: 'inline-flex' }}>
              <InputLeftElement>
                <Icon as={FiSearch} color={mode('fg.accent', 'fg.accent.muted')} fontSize="lg" />
              </InputLeftElement>
              <Input placeholder="Search Projects" variant={mode('flushed', 'flushed')} onChange={(event) => onSearch(event.target.value)} />
            </InputGroup>
            <CheckboxFilterPopover filterData={filterData} onSubmit={onChangeFilter} />
            <RadioIconButtonGroup defaultValue={currentType} size='md' onChange={(val: ProjectViewType) => onChangeType(val)}>
              <RadioIconButton value="card" aria-label="Tile view" icon={<FaThLarge />} />
              <RadioIconButton value="list" aria-label="List view" icon={<FaListUl />} />
            </RadioIconButtonGroup>
          </HStack>
        </Box>
      </Container>
    </Box>);
};
