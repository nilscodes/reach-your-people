import {
    Box,
    Flex,
    Heading,
    HStack,
    Icon,
    SimpleGrid,
    Stack,
    useColorModeValue,
  } from '@chakra-ui/react'
import { FaArrowRight } from 'react-icons/fa'
import { CategoryCard } from './CategoryCard'
import { categories } from './_data'
import { Account } from '../../lib/ryp-subscription-api';
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';

  type SubscriptionHomepageProps = {
    account: Account | null;
    subscriptions: any[];
  };

  export const SubscriptionsHomepage = (props: SubscriptionHomepageProps) => {
    const { t } = useTranslation('subscriptions');
    return (<Box
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
          <Heading size="lg">{t('subscriptionsTitle')}</Heading>
          <HStack spacing={{ base: '2', md: '3' }}>
            <NextLink href="/subscriptions/all"
              fontSize={{ base: 'md', md: 'lg' }}
              fontWeight="bold"
            >
              {t('subscriptionsSeeAll')}
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
            <CategoryCard key={category.type} category={category} />
          ))}
        </SimpleGrid>
      </Stack>
    </Box>);
  };