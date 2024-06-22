import { Button, IconButton, Tooltip } from '@chakra-ui/react';
import { MdLogin } from 'react-icons/md';
import NextLink from './NextLink';
import { useRouter } from 'next/navigation';
import useTranslation from 'next-translate/useTranslation';

export default function SignInToSubscribeButton({ fullButton }: { fullButton?: boolean }) {
  const { t } = useTranslation('common');
  const router = useRouter();
  const label = t('signInToSubscribe');
  // TODO keep track of project ID the user was subscribing to, or the current page
  if (!fullButton) {
    return (<Tooltip label={label} aria-label={label} hasArrow>
      <IconButton icon={<MdLogin />} aria-label={label} variant='outline' onClick={() => {
        router.push('/login');
      }} />
    </Tooltip>)
  } else {
    return (<Button leftIcon={<MdLogin />} aria-label={label} variant='outline'>
      {label}
    </Button>)
  }
}