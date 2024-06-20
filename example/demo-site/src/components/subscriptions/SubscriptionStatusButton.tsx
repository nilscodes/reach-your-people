import { Subscription } from '@/lib/types/Subscription';
import { DefaultSubscriptionStatus, SubscriptionStatus } from '@/lib/types/SubscriptionStatus';
import {
    IconButton, ButtonProps, Popover, PopoverTrigger, PopoverContent,
    PopoverHeader, PopoverBody, PopoverCloseButton, Radio, RadioGroup, Stack, Icon, Text, Tooltip
} from '@chakra-ui/react';
import { forwardRef, useState } from 'react';
import { MdCheck, MdNotInterested, MdVolumeMute } from 'react-icons/md';
import { RadioCard, RadioCardGroup } from '../RadioCardGroup';
import useTranslation from 'next-translate/useTranslation';
import { Translate } from 'next-translate';

interface SubscriptionStatusButtonProps extends ButtonProps {
    subscription?: Subscription;
    onStatusChange?: (status: SubscriptionStatus) => void;
}

const getButtonPropsFromSubscription = (t: Translate, subscription?: Subscription) => {
    if (subscription) {
        // Current status default and default status unsubscribed is the default assumptions and returned if no other status is found or subscription entry is not available
        if (subscription.currentStatus === SubscriptionStatus.Default && subscription.defaultStatus === DefaultSubscriptionStatus.Subscribed) {
            return {
                label: t('subscriptionStatus.subscribedDefault.label'),
                icon: <MdCheck />,
                variant: 'outline',
            }
        } else if (subscription.currentStatus === SubscriptionStatus.Subscribed) {
            return {
                label: t('subscriptionStatus.subscribedExplicitly.label'),
                icon: <MdCheck />,
                variant: 'solid',
            }
        } else if (subscription.currentStatus === SubscriptionStatus.Unsubscribed) {
            return {
                label: t('subscriptionStatus.unsubscribedExplicitly.label'),
                icon: <MdNotInterested />,
                variant: 'solid',
            }
        } else if (subscription.currentStatus === SubscriptionStatus.Muted) {
            return {
                label: t('subscriptionStatus.muted.label'),
                icon: <MdVolumeMute />,
                variant: 'solid',
            }
        }
    }
    return {
        label: t('subscriptionStatus.unsubscribedDefault.label'),
        icon: <MdNotInterested />,
        variant: 'outline',
    }
}

const getRadioCardOptions = (t: Translate, subscription?: Subscription) => {
    const options = [{
        value: SubscriptionStatus.Subscribed,
        label: t('subscriptionStatus.subscribedExplicitly.label'),
        description: t('subscriptionStatus.subscribedExplicitly.description'),
        icon: <MdCheck />,
        variant: 'solid',
    }, {
        value: SubscriptionStatus.Unsubscribed,
        label: t('subscriptionStatus.unsubscribedExplicitly.label'),
        description: t('subscriptionStatus.unsubscribedExplicitly.description'),
        icon: <MdNotInterested />,
        variant: 'solid',
    }];

    if (subscription?.defaultStatus === DefaultSubscriptionStatus.Subscribed) {
        options.unshift({
            value: SubscriptionStatus.Default,
            label: t('subscriptionStatus.subscribedDefault.label'),
            description: t('subscriptionStatus.subscribedDefault.description'),
            icon: <MdCheck />,
            variant: 'outline',
        });
    } else {
        options.unshift({
            value: SubscriptionStatus.Default,
            label: t('subscriptionStatus.unsubscribedDefault.label'),
            description: t('subscriptionStatus.unsubscribedDefault.description'),
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
    const { t } = useTranslation('projects');

    const handleChange = (nextValue: SubscriptionStatus) => {
        setRadioValue(nextValue);
        onStatusChange && onStatusChange(nextValue as SubscriptionStatus);
    };

    const { icon, label, variant } = getButtonPropsFromSubscription(t, subscription);
    const availableOptions = getRadioCardOptions(t, subscription);

    return (
        <Popover>
            {({ onClose }) => (
                <>
                    <PopoverTrigger>
                        <ButtonPopoverTrigger icon={icon} label={label} variant={variant} {...props} />
                    </PopoverTrigger>
                    <PopoverContent>
                        <PopoverHeader fontWeight="semibold" fontSize="lg" mt="1">{t('subscriptionStatus.label')}</PopoverHeader>
                        <PopoverCloseButton size="xs" />
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
