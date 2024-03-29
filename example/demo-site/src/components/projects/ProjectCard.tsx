import {
    AspectRatio,
    Box,
    Button,
    HStack,
    Image,
    Link,
    Skeleton,
    Stack,
    StackProps,
    Text,
    useColorModeValue,
  } from '@chakra-ui/react'
import Rating from './Rating'
import FavouriteButton from './FavouriteButton'
import PriceTag from './PriceTag'
import { Project } from '@/lib/types/Project'
import NextLink from '../NextLink'
import useTranslation from 'next-translate/useTranslation'
    
  interface Props {
    project: Project
    rootProps?: StackProps
  }
  
  export default function ProjectCard(props: Props) {
    const { project, rootProps } = props;
    const { t } = useTranslation('projects');
    const { name, logo } = project;

    return (
      <Stack spacing={{ base: '4', md: '5' }} {...rootProps}>
        <Box position="relative">
          <AspectRatio ratio={4 / 3}>
            <Image
              src={logo}
              alt={name}
              draggable="false"
              fallback={<Skeleton />}
              borderRadius={{ base: 'md', md: 'xl' }}
            />
          </AspectRatio>
          {/* <FavouriteButton
            position="absolute"
            top="4"
            right="4"
            aria-label={`Add ${name} to your favourites`}
          /> */}
        </Box>
        <Stack>
          <Stack spacing="1">
            <Text fontWeight="medium" color={useColorModeValue('gray.700', 'gray.400')}>
              {name}
            </Text>
            {/* <PriceTag price={price} salePrice={salePrice} currency="USD" /> */}
          </Stack>
          {/* <HStack>
            <Rating defaultValue={rating} size="sm" />
            <Text fontSize="sm" color={useColorModeValue('gray.600', 'gray.400')}>
              12 Reviews
            </Text>
          </HStack> */}
        </Stack>
        <Stack align="center">
          <NextLink href={`/projects/${project.id}/publish`} w="100%">
            <Button variant="outline" width="full">
              {t('publishAnnouncementButton')}
            </Button>
          </NextLink>
          <NextLink href={`/projects/${project.id}/edit`}
            textDecoration="underline"
            fontWeight="medium"
            color={useColorModeValue('gray.600', 'gray.400')}
          >
            {t('editProjectButton')}
          </NextLink>
        </Stack>
      </Stack>
    )
  }