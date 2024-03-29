import { Button } from '@chakra-ui/react';
import { signOut, useSession } from "next-auth/react";
import useTranslation from 'next-translate/useTranslation';
import { useRouter } from 'next/navigation';

export default function AuthButton() {
  const { data: session } = useSession();
  const router = useRouter();
  const { t } = useTranslation('common');

  const username = session?.user?.name;

  if (session) {
    return <Button variant="outline" onClick={() => signOut({
      callbackUrl: "/",
    })}>{t('signOut', { username })}</Button>
  } else {
    return <Button onClick={() => router.push('/login') }>{t('signIn')}</Button>
  }
}
