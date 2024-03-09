import { Account } from "@/lib/ryp-api";
import SignInToSubscribeButton from "../SignInToSubscribeButton";
import SubscriptionStatusButton from "./SubscriptionStatusButton";
import { Project } from "@/lib/types/Project";
import { Subscription } from "@/lib/types/Subscription";
import FavoriteButton from "./FavoriteButton";

type ProjectCardProps = {
    account: Account | null;
    project: Project;
    subscription?: Subscription;
};

export default function SubscriptionActions({ account, project, subscription }: ProjectCardProps) {
    if (account != null) {
        return <>
            <SubscriptionStatusButton subscription={subscription} />
        </>;
    } else {
        return <SignInToSubscribeButton />;
    }
};