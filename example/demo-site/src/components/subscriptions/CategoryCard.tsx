import {
    Box,
    BoxProps,
    Flex,
    Heading,
    HStack,
    Icon,
    Image,
    Link,
    Skeleton,
    Stack,
    Text,
  } from '@chakra-ui/react'
  import { FaChevronRight } from 'react-icons/fa'
  import { Category } from './_data'
import NextLink from '../NextLink'
import { useRouter } from 'next/navigation'
  
  interface Props {
    category: Category
    rootProps?: BoxProps
  }
  
  export const CategoryCard = (props: Props) => {
    const { category, rootProps } = props
    const router = useRouter()
    return (
      <Box
        position="relative"
        key={category.name}
        borderRadius="xl"
        overflow="hidden"
        minH={{ base: 'sm', lg: 'auto' }}
        {...rootProps}
      >
        <Image
            src={category.imageUrl}
            height="full"
            objectFit="cover"
            alt={category.name}
            fallback={<Skeleton />}
        />
        <Box
            position="absolute"
            inset="0"
            bg="linear-gradient(180deg, rgba(0, 0, 0, 0) 47.92%, #000000 100%)"
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
            onClick={() => router.push('/subscriptions/nfts')}
        >
            <Stack spacing="5">
                <Stack spacing="1">
                <Heading fontSize="2xl" fontWeight="extrabold">
                    {category.name}
                </Heading>
                <Text fontSize="lg" fontWeight="medium">
                    {category.description}
                </Text>
                </Stack>
                <HStack>
                <NextLink href="." fontSize="lg" fontWeight="bold" textDecoration="underline">
                    Manage
                </NextLink>
                <Icon as={FaChevronRight} />
                </HStack>
            </Stack>
        </Flex>
      </Box>
    )
  }