import { Account } from "../../lib/ryp-subscription-api";
import SignInToSubscribeButton from "../SignInToSubscribeButton";
import SubscriptionStatusButton from "./SubscriptionStatusButton";
import { Project } from "@/lib/types/Project";
import { Subscription } from "@/lib/types/Subscription";
import { useApi } from "@/contexts/ApiProvider";
import { SubscriptionStatus } from "@/lib/types/SubscriptionStatus";
import { useSubscriptions } from "@/contexts/SubscriptionsProvider";

type ProjectCardProps = {
    account: Account | null;
    project: Project;
    subscription?: Subscription;
};

export default function SubscriptionActions({ account, project, subscription }: ProjectCardProps) {
    const api = useApi();
    const { setSubscriptions } = useSubscriptions();

    const changeSubscriptionPreference = async (status: SubscriptionStatus) => {
        await api.changeSubscriptionPreference(project.id, status);
        setSubscriptions(await api.getSubscriptions());
    };

    if (account != null) {
        return <>
            <SubscriptionStatusButton subscription={subscription} onStatusChange={changeSubscriptionPreference} />
        </>;
    } else {
        return <SignInToSubscribeButton />;
    }
};