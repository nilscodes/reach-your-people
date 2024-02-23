import { getServerSession } from "next-auth";
import { NextResponse } from "next/server";
import { config } from "auth";

export async function GET() {
  const session = await getServerSession(config);
  
  return NextResponse.json({
    name: session?.user?.name ?? "Nobody"
  });
}