import { Circle, Icon, SquareProps } from '@chakra-ui/react'
import { HiCheck } from 'react-icons/hi'

interface RadioCircleProps extends SquareProps {
  isCompleted: boolean
  isActive: boolean
}

export const StepCircle = (props: RadioCircleProps) => {
  const { isCompleted, isActive, ...rest } = props
  return (
    <Circle
      size="8"
      bg={isCompleted ? 'accent' : 'inherit'}
      borderWidth={isCompleted ? '0' : '2px'}
      borderColor={isActive ? 'accent' : 'inherit'}
      {...rest}
    >
      {isCompleted ? (
        <Icon as={HiCheck} color="fg.inverted" boxSize="5" />
      ) : (
        <Circle bg={isActive ? 'accent' : 'border'} size="3" />
      )}
    </Circle>
  )
}