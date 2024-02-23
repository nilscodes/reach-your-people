import { redirect } from "next/navigation";
import { getServerSession } from "next-auth";

export default async function Home() {
  const session = await getServerSession();

  if (!session || !session.user) {
    redirect("/api/auth/signin");
  }

  return (
    <main>
      {session?.user?.name && (
        <div>{session?.user?.name}</div>
      )}
    </main>
  );
}
