import {
  Heading,
  Icon,
  List,
  ListIcon,
  ListItem,
  Text,
  useColorModeValue,
  VStack,
} from '@chakra-ui/react'
import * as React from 'react'
import { HiCheckCircle } from 'react-icons/hi'
import Card from './Card'
import { CardProps } from './CardProps'

interface PricingCardData {
  features: string[]
  name: string
  price: React.ReactElement,
  priceSub?: React.ReactElement,
}

interface PricingCardProps extends CardProps {
  data: PricingCardData
  icon: React.ElementType
  button?: React.ReactElement
}

export default function PricingCard(props: PricingCardProps) {
  const { data, icon, button, ...rest } = props
  const { features, price, priceSub, name } = data
  const accentColor = useColorModeValue('brand.600', 'brand.200')

  return (
    <Card rounded={{ sm: 'xl' }} {...rest}>
      <VStack spacing={6}>
        <Icon aria-hidden as={icon} fontSize="4xl" color={accentColor} />
        <Heading size="xs" fontWeight="extrabold">
          {name}
        </Heading>
      </VStack>
      <VStack fontWeight="extrabold" color={accentColor} my="8">
        <Heading size="3xl" fontWeight="inherit" lineHeight="0.9em">
          {price}
        </Heading>
        {priceSub}
      </VStack>
      <List spacing="4" mb="8" maxW="36ch" mx="auto">
        {features.map((feature, index) => (
          <ListItem fontWeight="medium" key={index}>
            <ListIcon fontSize="xl" as={HiCheckCircle} marginEnd={2} color={accentColor} />
            {feature}
          </ListItem>
        ))}
      </List>
      {button}
    </Card>
  )
}