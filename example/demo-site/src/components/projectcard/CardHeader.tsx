import { Flex, FlexProps, Heading } from '@chakra-ui/react';
import FavoriteButton from '../subscriptions/FavoriteButton';
import { Subscription } from '@/lib/types/Subscription';

interface CardHeaderProps extends FlexProps {
  favoriteButton?: boolean;
  title: string;
  subscription?: Subscription;
}

export default function CardHeader(props: CardHeaderProps) {
  const { title, subscription, favoriteButton, ...flexProps } = props;
  return (
    <Flex justifyContent="start" alignItems="center" {...flexProps} mb="4">
      <Heading size="sm" fontWeight="extrabold" letterSpacing="tight" marginEnd="2" noOfLines={2}>
        {title}
      </Heading>
      {favoriteButton && <FavoriteButton subscription={subscription} />}
    </Flex>
  );
};
