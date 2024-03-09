import { Button } from '@chakra-ui/react';
import { signOut, useSession } from "next-auth/react";
import { useRouter } from 'next/navigation';

export default function AuthButton() {
  const { data: session } = useSession();
  const router = useRouter();

  const username = session?.user?.name;

  if (session) {
    return <Button variant="outline" onClick={() => signOut({
      callbackUrl: "/",
    })}>Sign out ({username})</Button>
  } else {
    return <Button onClick={() => router.push('/login') }>Sign in</Button>
  }
}
