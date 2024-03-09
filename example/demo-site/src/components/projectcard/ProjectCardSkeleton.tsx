import {
  SkeletonCircle,
  SkeletonText,
  Stack,
} from '@chakra-ui/react';
import Card from './Card';

export default function ProjectCardSkeleton() {
  return(<Card>
    <Stack direction={{ base: 'column', md: 'row' }} spacing={{ base: '4', md: '10' }}>
      <SkeletonCircle size="20" />
      <SkeletonText mt="4" noOfLines={6} spacing="4" w="full" />
    </Stack>
  </Card>);
}
