import { Box, Button, Spinner, Stack, Text } from "@chakra-ui/react"
import useTranslation from "next-translate/useTranslation"
import { GiCorkedTube } from "react-icons/gi"
import { useStepContext } from "../stepper/StepContext"
import { useApi } from "@/contexts/ApiProvider"
import { useEffect, useState } from "react"
import { TestStatus } from "@/lib/types/TestStatus"
import { MdCheck, MdError } from "react-icons/md"
import NextLink from "../NextLink"

const telegramBotLink = `https://telegram.me/${process.env.NEXT_PUBLIC_AUTH_TELEGRAM_BOT_NAME}`;

function TelegramStep1() {
  const { t } = useTranslation('accounts')
  const { setStep, metadata, setMetadata } = useStepContext()
  const [sendTestDisabled, setSendTestDisabled] = useState(false)
  const api = useApi()

  const sendTestMessage = async () => {
    try {
      setSendTestDisabled(true)
      const testAnnouncement = await api.sendTestNotification(metadata.externalAccountId as number, 'telegram')
      setMetadata({ ...metadata, testAnnouncementId: testAnnouncement.id! })
      setStep(1)
    } catch (error) {
      console.error(error)
    }
  }

  return (<Stack spacing='2'>
    <Text>{t('notificationTest.telegram.step1.description')}</Text>
    <Box>
      <Button
        leftIcon={<GiCorkedTube />}
        onClick={sendTestMessage}
        isDisabled={sendTestDisabled}
      >{t('notificationTest.telegram.step1.sendTestMessage')}</Button>
    </Box>
  </Stack>)
}

function TelegramStep2() {
  const { t } = useTranslation('accounts')
  const { setStep, metadata, setMetadata } = useStepContext()
  const [sendStatus, setSendStatus] = useState(TestStatus.Waiting)
  const api = useApi()
  
  useEffect(() => {
    const interval = setInterval(async () => {
      const sendStatus = await api.getTestNotificationStatus(metadata.externalAccountId as number, metadata.testAnnouncementId as string)
      if (sendStatus.status !== TestStatus.Waiting) {
        clearInterval(interval)
        setSendStatus(sendStatus.status)
        setMetadata({ ...metadata, status: sendStatus.status })
        setStep(2)
      }
    }, 5000)
    return () => clearInterval(interval)
  }, [api])

  return (<Stack spacing='2'>
    <Text>{t('notificationTest.telegram.step2.description')}</Text>
    <Box>
      {sendStatus === TestStatus.Waiting && (<Spinner />)}
      {sendStatus === TestStatus.Delivered && (<MdCheck size="3em" />)}
      {sendStatus === TestStatus.Failed && (<MdError size="3em" />)}
    </Box>
  </Stack>)
}

function TelegramStep3() {
  const { t } = useTranslation('accounts')
  const { metadata } = useStepContext()
  const status = metadata.status as TestStatus

  return (<Stack spacing='2'>
    <Text>{t(`notificationTest.telegram.step3.description${status === TestStatus.Delivered ? 'Success' : 'Error'}`, { telegramBotLink })}</Text>
    <NextLink href={telegramBotLink} isExternal>{telegramBotLink}</NextLink>
  </Stack>)
}

export const telegramSteps = [
  {
    title: 'notificationTest.telegram.step1.title',
    children: <TelegramStep1 />
  },
  {
    title: 'notificationTest.telegram.step2.title',
    children: <TelegramStep2 />
  },
  {
    title: 'notificationTest.telegram.step3.title',
    children: <TelegramStep3 />
  }
]