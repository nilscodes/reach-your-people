import {
  Box,
  BoxProps,
  Flex,
  Heading,
  HStack,
  Icon,
  Image,
  Skeleton,
  Stack,
  Text,
} from '@chakra-ui/react'
import { FaChevronRight } from 'react-icons/fa'
import { Category } from './_data'
import NextLink from '../NextLink'
import { useRouter } from 'next/navigation'
import useTranslation from 'next-translate/useTranslation'

interface Props {
  category: Category
  rootProps?: BoxProps
}

export const CategoryCard = (props: Props) => {
  const { category, rootProps } = props;
  const router = useRouter();
  const { t } = useTranslation('projects');
  const { t: tc } = useTranslation('common');
  return (
    <Box
      position="relative"
      key={category.type}
      borderRadius="xl"
      overflow="hidden"
      minH={{ base: 'sm', lg: 'auto' }}
      {...rootProps}
    >
      <Image
        src={category.imageUrl}
        height="full"
        objectFit="cover"
        alt={t(`categories.${category.type}.name`)}
        fallback={<Skeleton />}
      />
      <Box
        position="absolute"
        inset="0"
        bg="linear-gradient(180deg, rgba(0, 0, 0, 0) 27.92%, #000000 100%)"
        boxSize="full"
      />
      <Flex
        color="white"
        direction="column-reverse"
        position="absolute"
        inset="0"
        boxSize="full"
        px={{ base: '4', md: '8' }}
        py={{ base: '6', md: '8', lg: '10' }}
        cursor="pointer"
        onClick={() => router.push(`/projects/${category.type}`)}
      >
        <Stack spacing="5">
          <Stack spacing="1">
            <Heading fontSize="2xl" fontWeight="extrabold">
              {tc(`categories.${category.type}.name`)}
            </Heading>
            <Text fontSize="lg" fontWeight="medium">
              {tc(`categories.${category.type}.description`)}
            </Text>
          </Stack>
          <HStack>
            <NextLink href={`/projects/${category.type}`} fontSize="lg" fontWeight="bold" textDecoration="underline">
              {t('manageSubscriptions')}
            </NextLink>
            <Icon as={FaChevronRight} />
          </HStack>
        </Stack>
      </Flex>
    </Box>
  )
}