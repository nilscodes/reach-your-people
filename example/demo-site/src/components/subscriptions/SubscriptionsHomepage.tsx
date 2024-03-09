import {
    Box,
    Button,
    Flex,
    Heading,
    HStack,
    Icon,
    Link,
    SimpleGrid,
    Stack,
    useColorModeValue,
  } from '@chakra-ui/react'
  import { FaArrowRight } from 'react-icons/fa'
  import { CategoryCard } from './CategoryCard'
  import { categories } from './_data'
import { Account } from '@/lib/ryp-api';
import NextLink from '../NextLink';

  type SubscriptionHomepageProps = {
    account: Account | null;
    subscriptions: any[];
  };
  
  export const SubscriptionsHomepage = (props: SubscriptionHomepageProps) => (
    <Box
      maxW="7xl"
      mx="auto"
      px={{ base: '4', md: '8', lg: '12' }}
      py={{ base: '6', md: '8', lg: '12' }}
    >
      <Stack spacing={{ base: '6', md: '8', lg: '12' }}>
        <Flex
          justify="space-between"
          align={{ base: 'start', md: 'center' }}
          direction={{ base: 'column', md: 'row' }}
        >
          <Heading size="lg">Subscriptions</Heading>
          <HStack spacing={{ base: '2', md: '3' }}>
            <NextLink href="/subscriptions/all"
              fontSize={{ base: 'md', md: 'lg' }}
              fontWeight="bold"
            >
              See all
            </NextLink>
            <Icon
              as={FaArrowRight}
              color={useColorModeValue('fg.accent', 'fg.accent')}
              fontSize={{ base: 'sm', md: 'md' }}
            />
          </HStack>
        </Flex>
        <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} gap={{ base: '8', lg: '16' }}>
          {categories.map((category) => (
            <CategoryCard key={category.name} category={category} />
          ))}
        </SimpleGrid>
      </Stack>
    </Box>
  )