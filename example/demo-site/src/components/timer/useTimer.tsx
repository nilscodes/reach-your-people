import { useInterval } from '@chakra-ui/react'
import { useEffect, useState } from 'react'

interface Props {
  expiresInSeconds: number
}

// For more sophisticated timer hooks checkout https://github.com/amrlabib/react-timer-hook
export const useTimer = (props: Props) => {
  const { expiresInSeconds } = props;
  const [seconds, setSeconds] = useState(getSecondsFromExpiry(expiresInSeconds));
  const [loadedInFrontend, setLoadedInFrontend] = useState(false);

  useEffect(() => {
    setLoadedInFrontend(true);
  }, []);

  useInterval(() => setSeconds(getSecondsFromExpiry(expiresInSeconds)), 1000)

  return {
    seconds: loadedInFrontend ? Math.floor(seconds % 60) : 0,
    minutes: loadedInFrontend ? Math.floor((seconds % (60 * 60)) / 60) : 0,
    hours: loadedInFrontend ? Math.floor((seconds % (60 * 60 * 24)) / (60 * 60)) : 0,
    days: loadedInFrontend ? Math.floor(seconds / (60 * 60 * 24)) : 0,
  }
}

const getSecondsFromExpiry = (expire: number) => Math.round((expire - new Date().getTime()) / 1000)