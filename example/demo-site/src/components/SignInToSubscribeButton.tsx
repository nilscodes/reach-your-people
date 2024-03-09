import { IconButton, Tooltip } from '@chakra-ui/react';
import { MdLogin } from 'react-icons/md';
import NextLink from './NextLink';
import { useRouter } from 'next/navigation';

export default function SignInToSubscribeButton() {
    const router = useRouter();
    // TODO keep track of project ID the user was subscribing to, or the current page
    return (<Tooltip label='Sign in to subscribe' aria-label='Sign in to subscribe' hasArrow>
        <IconButton icon={<MdLogin />} aria-label='Sign in to subscribe' variant='outline' onClick={() => {
            router.push('/login');
        }} />
    </Tooltip>)
}