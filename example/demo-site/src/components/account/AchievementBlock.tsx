import { iconMap } from '@/lib/achievements'
import { Achievement } from '@/lib/types/Achievement'
import { Box, BoxProps, Divider, HStack, Heading, Icon, Progress, Stack, Text, useColorModeValue } from '@chakra-ui/react'
import useTranslation from 'next-translate/useTranslation'
import { MdAccountBox, MdOutlineShare } from 'react-icons/md'

interface AchievementBlockProps extends BoxProps {
  achievement: Achievement
}

interface AchievementProgressProps extends BoxProps {
  achievement: Achievement
  value: number
  limit: number
}

interface AchievementYesNoProps extends BoxProps {
  achievement: Achievement
  achieved: boolean
}

const AchievementProgress = ({ achievement, value, limit, ...boxProps }: AchievementProgressProps) => {
  const { t } = useTranslation('accounts')
  const achieved = value >= limit;
  const iconColor = useColorModeValue(achieved ? 'green.500' : 'fg.muted', achieved ? 'green.300' : 'fg.muted')
  return (
    <Box bg="bg.surface" boxShadow="sm" {...boxProps} borderRadius="lg" w={{ base: '100%', md: 'md' }} mx="auto">
      <Box px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }}>
        <HStack justifyContent='space-between'>
          <Stack>
            <Text textStyle="sm" color="fg.muted">
              {t(achievement.title)}
            </Text>
            <Stack direction="row" align="baseline">
              <Heading size={{ base: 'sm', md: 'md' }}>{value}</Heading>
              <Text aria-hidden fontWeight="semibold" color="fg.muted">
                / {limit}
              </Text>
              <Box srOnly>out of {limit}</Box>
            </Stack>
          </Stack>
          <Icon as={iconMap[achievement.icon]} color={iconColor} boxSize="3em" />
        </HStack>
      </Box>
      <Progress value={(value / limit) * 100} size="xs" borderRadius="none" bg="bg.surface" />
      <Divider />
      <Box px={{ base: '4', md: '6' }} py="4">
        <Text textStyle="sm" color="fg.muted">
          {t(achievement.description)}
        </Text>
      </Box>
    </Box>
  )
}

const AchievementYesNo = ({ achievement, achieved, ...boxProps }: AchievementYesNoProps) => {
  const iconColor = useColorModeValue(achieved ? 'green.500' : 'fg.muted', achieved ? 'green.300' : 'fg.muted')
  const { t } = useTranslation('accounts')
  return (
    <Box bg='bg.surface' boxShadow="sm" {...boxProps} borderRadius="lg" w={{ base: '100%', md: 'md' }} mx="auto">
      <Box px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }}>
        <HStack justifyContent='space-between'>
          <Stack>
            <Text textStyle="sm" color="fg.muted">
              {t(achievement.title)}
            </Text>
            <Stack direction="row" align="baseline">
              <Heading size={{ base: 'sm', md: 'md' }}>{achieved ? 1 : 0}</Heading>
              <Text aria-hidden fontWeight="semibold" color="fg.muted">
                / 1
              </Text>
              <Box srOnly>{t('achievements.achievedScreenReader')}</Box>
            </Stack>
          </Stack>
          <Icon as={iconMap[achievement.icon]} color={iconColor} boxSize="3em" />
        </HStack>
      </Box>
      <Divider />
      <Box px={{ base: '4', md: '6' }} py="4">
        <Text textStyle="sm" color="fg.muted">
          {t(achievement.description)}
        </Text>
      </Box>
    </Box>
  )
}

export default function AchievementBlock(props: AchievementBlockProps) {
  const { achievement, ...boxProps } = props
  if (achievement.type === 'numeric') {
    const limit = achievement.maxPoints ?? 1;
    const value = Math.min(limit, achievement.points ?? 0);
    return <AchievementProgress achievement={achievement} value={value} limit={limit} {...boxProps} />
  } else {
    const achieved = achievement.achieved ?? false;
    return <AchievementYesNo achievement={achievement} achieved={achieved} {...boxProps} />
  }
}