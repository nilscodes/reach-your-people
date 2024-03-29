import { Subscription } from '@/lib/types/Subscription';
import { DefaultSubscriptionStatus, SubscriptionStatus } from '@/lib/types/SubscriptionStatus';
import {
    IconButton, ButtonProps, Popover, PopoverTrigger, PopoverContent,
    PopoverHeader, PopoverBody, PopoverCloseButton, Radio, RadioGroup, Stack, Icon, Text, Tooltip
} from '@chakra-ui/react';
import { forwardRef, useState } from 'react';
import { MdCheck, MdNotInterested, MdVolumeMute } from 'react-icons/md';
import { RadioCard, RadioCardGroup } from '../RadioCardGroup';

interface SubscriptionStatusButtonProps extends ButtonProps {
    subscription?: Subscription;
    onStatusChange?: (status: SubscriptionStatus) => void;
}

const getButtonPropsFromSubscription = (subscription?: Subscription) => {
    if (subscription) {
        // Current status default and default status unsubscribed is the default assumptions and returned if no other status is found or subscription entry is not available
        if (subscription.currentStatus === SubscriptionStatus.Default && subscription.defaultStatus === DefaultSubscriptionStatus.Subscribed) {
            return {
                label: 'Subscribed (Default)',
                icon: <MdCheck />,
                variant: 'outline',
            }
        } else if (subscription.currentStatus === SubscriptionStatus.Subscribed) {
            return {
                label: 'Subscribed',
                icon: <MdCheck />,
                variant: 'solid',
            }
        } else if (subscription.currentStatus === SubscriptionStatus.Unsubscribed) {
            return {
                label: 'Unsubscribed',
                icon: <MdNotInterested />,
                variant: 'solid',
            }
        } else if (subscription.currentStatus === SubscriptionStatus.Muted) {
            return {
                label: 'Muted',
                icon: <MdVolumeMute />,
                variant: 'solid',
            }
        }
    }
    return {
        label: 'Unsubscribed (Default)',
        icon: <MdNotInterested />,
        variant: 'outline',
    }
}

const getRadioCardOptions = (subscription?: Subscription) => {
    const options = [{
        value: SubscriptionStatus.Subscribed,
        label: 'Subscribed',
        description: 'Excplicitly subscribed to this project regardless of wallet settings',
        icon: <MdCheck />,
        variant: 'solid',
    }, {
        value: SubscriptionStatus.Unsubscribed,
        label: 'Unsubscribed',
        description: 'Excplcitly unsubscribed from this project regardless of wallet settings',
        icon: <MdNotInterested />,
        variant: 'solid',
    }];

    if (subscription?.defaultStatus === DefaultSubscriptionStatus.Subscribed) {
        options.unshift({
            value: SubscriptionStatus.Default,
            label: 'Default (Subscribed)',
            description: 'Subscribed by default due to wallet settings',
            icon: <MdCheck />,
            variant: 'outline',
        });
    } else {
        options.unshift({
            value: SubscriptionStatus.Default,
            label: 'Default (Unsubscribed)',
            description: 'Unsubscribed by default due to wallet settings',
            icon: <MdNotInterested />,
            variant: 'outline',
        });
    }
    return options;
};

type ButtonPopoverTriggerProps = ButtonProps & {
    icon: JSX.Element;
    label: string;
};

const ButtonPopoverTrigger = forwardRef(function TriggerRef(props: ButtonPopoverTriggerProps, ref) {
    const { icon, label, variant, ...rest } = props;
    return (<Tooltip label={label} aria-label={label} hasArrow >
        <IconButton icon={icon} {...rest} aria-label={label} variant={variant} ref={ref} />
    </Tooltip>)
});

export default function SubscriptionStatusButton({ subscription, onStatusChange, ...props }: SubscriptionStatusButtonProps) {
    const [, setRadioValue] = useState(subscription?.currentStatus || SubscriptionStatus.Default);

    const handleChange = (nextValue: SubscriptionStatus) => {
        setRadioValue(nextValue);
        onStatusChange && onStatusChange(nextValue as SubscriptionStatus);
    };

    const { icon, label, variant } = getButtonPropsFromSubscription(subscription);
    const availableOptions = getRadioCardOptions(subscription);

    return (
        <Popover>
            {({ onClose }) => (
                <>
                    <PopoverTrigger>
                        <ButtonPopoverTrigger icon={icon} label={label} variant={variant} {...props} />
                    </PopoverTrigger>
                    <PopoverContent>
                        <PopoverHeader fontWeight="semibold">Subscription Status</PopoverHeader>
                        <PopoverCloseButton />
                        <PopoverBody>
                            <RadioCardGroup
                                defaultValue={subscription?.currentStatus ?? SubscriptionStatus.Default}
                                spacing="3"
                                onChange={(nextValue: SubscriptionStatus) => {
                                    handleChange(nextValue);
                                    onClose()
                                }}>
                                {availableOptions.map((option) => (
                                    <RadioCard key={option.value} value={option.value} icon={option.icon} iconVariant={option.variant}>
                                        <Text color="fg.emphasized" fontWeight="bold" fontSize="sm">
                                            {option.label}
                                        </Text>
                                        <Text color="fg.muted" textStyle="sm">
                                            {option.description}
                                        </Text>
                                    </RadioCard>
                                ))}
                            </RadioCardGroup>
                        </PopoverBody>
                    </PopoverContent>
                </>
            )}
        </Popover>
    );
};
