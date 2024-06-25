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
import { makeCdnUrl } from '@/lib/cdn'
import { MdAnnouncement, MdList } from 'react-icons/md'
    
interface Props {
  project: Project
  rootProps?: StackProps
}

export default function ProjectCard(props: Props) {
  const { project, rootProps } = props;
  const { t } = useTranslation('publish');
  const { name, logo } = project;

  return (
    <Stack spacing={{ base: '4', md: '5' }} {...rootProps}>
      <Box position="relative">
        <Box
          width="100%"
          paddingTop="75%" // This makes the box a square by maintaining aspect ratio
          position="relative"
          overflow="hidden"
        >
          <Image
            src={makeCdnUrl(logo)}
            alt={name}
            position="absolute"
            top="0"
            left="0"
            draggable="false"
            fallback={<Skeleton />}
            borderRadius={{ base: 'md', md: 'xl' }}
            objectFit='scale-down'
            width='100%'
            height='100%'
          />
        </Box>
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
        <NextLink href={`/publish/${project.id}/publish`} w="100%">
          <Button leftIcon={<MdAnnouncement />} variant="outline" width="full">
            {t('publishAnnouncementButton')}
          </Button>
        </NextLink>
        <NextLink href={`/publish/${project.id}/announcements`} w="100%">
          <Button leftIcon={<MdList />} variant="outline" width="full">
            {t('viewAnnouncementsButton')}
          </Button>
        </NextLink>
        {/* <NextLink href={`/publish/${project.id}/edit`}
          textDecoration="underline"
          fontWeight="medium"
          color={useColorModeValue('gray.600', 'gray.400')}
        >
          {t('editProjectButton')}
        </NextLink> */}
      </Stack>
    </Stack>
  )
}