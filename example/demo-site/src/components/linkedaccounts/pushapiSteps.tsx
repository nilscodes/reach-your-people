import { Box, Button, Spinner, Stack, Text } from "@chakra-ui/react"
import useTranslation from "next-translate/useTranslation"
import { GiCorkedTube } from "react-icons/gi"
import { useStepContext } from "../stepper/StepContext"
import { useApi } from "@/contexts/ApiProvider"
import { useEffect, useState } from "react"
import { TestStatus } from "@/lib/types/TestStatus"
import { MdCheck, MdError } from "react-icons/md"

function PushApiStep1() {
  const { t } = useTranslation('accounts')
  const { setStep, metadata, setMetadata } = useStepContext()
  const [sendTestDisabled, setSendTestDisabled] = useState(false)
  const api = useApi()

  const sendTestMessage = async () => {
    try {
      setSendTestDisabled(true)
      const testAnnouncement = await api.sendTestNotification(metadata.externalAccountId as number, 'pushapi')
      setMetadata({ ...metadata, testAnnouncementId: testAnnouncement.id! })
      setStep(1)
    } catch (error) {
      console.error(error)
    }
  }

  return (<Stack spacing='2'>
    <Text>{t('notificationTest.pushapi.step1.description')}</Text>
    <Box>
      <Button
        leftIcon={<GiCorkedTube />}
        onClick={sendTestMessage}
        isDisabled={sendTestDisabled}
      >{t('notificationTest.pushapi.step1.sendTestMessage')}</Button>
    </Box>
  </Stack>)
}

function PushApiStep2() {
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
    <Text>{t('notificationTest.pushapi.step2.description')}</Text>
    <Box>
      {sendStatus === TestStatus.Waiting && (<Spinner />)}
      {sendStatus === TestStatus.Delivered && (<MdCheck size="3em" />)}
      {sendStatus === TestStatus.Failed && (<MdError size="3em" />)}
    </Box>
  </Stack>)
}

function PushApiStep3() {
  const { t } = useTranslation('accounts')
  const { metadata } = useStepContext()
  const status = metadata.status as TestStatus

  return (<Stack spacing='2'>
    <Text>{t(`notificationTest.pushapi.step3.description${status === TestStatus.Delivered ? 'Success' : 'Error'}`)}</Text>
  </Stack>)
}

export const pushapiSteps = [
  {
    title: 'notificationTest.pushapi.step1.title',
    children: <PushApiStep1 />
  },
  {
    title: 'notificationTest.pushapi.step2.title',
    children: <PushApiStep2 />
  },
  {
    title: 'notificationTest.pushapi.step3.title',
    children: <PushApiStep3 />
  }
]