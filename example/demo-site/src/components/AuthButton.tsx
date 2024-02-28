import { Button } from '@chakra-ui/react';
import { signOut, useSession } from "next-auth/react";
import NextLink from './NextLink';

export default function AuthButton() {
  const { data: session } = useSession();

  const username = session?.user?.name;

  if (session) {
    return <Button onClick={() => signOut({
      callbackUrl: "/",
    })}>Sign out ({username})</Button>
  } else {
    return <NextLink href="/login"><Button>Sign in</Button></NextLink>
  }
}
