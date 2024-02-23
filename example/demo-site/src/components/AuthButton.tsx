"use client";

import { Button } from '@chakra-ui/react';
import { signIn, signOut, useSession } from "next-auth/react";

export default function AuthButton() {
  const { data: session } = useSession();

  const username = session?.user?.name;

  if (session) {
    return <Button onClick={() => signOut()}>Sign out ({username})</Button>
  } else {
    return <Button onClick={() => signIn()}>Sign in</Button>
  }
}
