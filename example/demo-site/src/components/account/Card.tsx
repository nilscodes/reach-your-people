import { Box, useColorModeValue } from '@chakra-ui/react'
import * as React from 'react'
import CardBadge from './CardBadge'
import useTranslation from 'next-translate/useTranslation';
import { CardProps } from './CardProps';

export default function Card(props: CardProps) {
  const { t } = useTranslation('accounts');
  const { children, isPopular, ...rest } = props
  return (
    <Box
      bg={useColorModeValue('white', 'gray.700')}
      position="relative"
      px="6"
      pb="6"
      pt="16"
      overflow="hidden"
      shadow="lg"
      maxW="lg"
      width="100%"
      {...rest}
    >
      {isPopular && <CardBadge>{t('premiumAccount.popular')}</CardBadge>}
      {children}
    </Box>
  )
}