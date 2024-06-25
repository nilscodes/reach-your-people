import { Badge, Box, Button, Divider, Heading, HStack, Icon, Stack, Text, Tooltip } from '@chakra-ui/react'
import { FiArrowDownRight, FiArrowUpRight, FiHelpCircle, FiMoreVertical } from 'react-icons/fi'

interface StatProps {
  label: string;
  value: string;
  tooltip?: string;
//   delta: {
//     value: string
//     isUpwardsTrend: boolean
//   }
}
// export const Stat = (props: StatProps) => {
//   const { label, value, /*delta,*/ ...boxProps } = props
//   return (
//     <Box
//       px={{ base: '4', md: '6' }}
//       py={{ base: '5', md: '6' }}
//       bg="bg.surface"
//       borderRadius="lg"
//       boxShadow="sm"
//       {...boxProps}
//     >
//       <Stack>
//         <HStack justify="space-between">
//           <Text textStyle="sm" color="fg.muted">
//             {label}
//           </Text>
//           {/* <Icon as={FiMoreVertical} boxSize="5" color="fg.muted" /> */}
//         </HStack>
//         <HStack justify="space-between">
//           <Heading size={{ base: 'sm', md: 'md' }}>{value}</Heading>
//           {/* <Badge variant="pill" colorScheme={delta.isUpwardsTrend ? 'green' : 'red'}>
//             <HStack spacing="1">
//               <Icon as={delta.isUpwardsTrend ? FiArrowUpRight : FiArrowDownRight} />
//               <Text>{delta.value}</Text>
//             </HStack>
//           </Badge> */}
//         </HStack>
//       </Stack>
//     </Box>
//   )
// }

export default function Stat(props: StatProps) {
  const { label, value, tooltip, /* delta, */ ...boxProps } = props
  return (
    <Box bg="bg.surface" borderRadius="lg" boxShadow="sm" {...boxProps}>
      <Box px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }}>
        <Stack>
          <HStack justify="space-between">
            <Text textStyle="sm" color="fg.muted">
              {label}
            </Text>
            {tooltip && (<Tooltip label={tooltip} aria-label={tooltip}>
              <span><Icon as={FiHelpCircle} boxSize="5" color="fg.muted" /></span>
            </Tooltip>)}
          </HStack>
          <HStack justify="space-between">
            <Heading size={{ base: 'sm', md: 'md' }}>{value}</Heading>
            {/* <Badge variant="pill" colorScheme={delta.isUpwardsTrend ? 'green' : 'red'}>
              <HStack spacing="1">
                <Icon as={delta.isUpwardsTrend ? FiArrowUpRight : FiArrowDownRight} />
                <Text>{delta.value}</Text>
              </HStack>
            </Badge> */}
          </HStack>
        </Stack>
      </Box>
      {/* <Divider />
      <Box px={{ base: '4', md: '6' }} py="4">
        <Button variant="text" colorScheme="blue" size="sm">
          Learn more
        </Button>
      </Box> */}
    </Box>
  )
}