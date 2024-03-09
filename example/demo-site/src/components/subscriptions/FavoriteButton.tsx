import { Subscription } from "@/lib/types/Subscription";
import { IconButton, Tooltip, useColorModeValue } from "@chakra-ui/react";
import { MdStar, MdStarOutline } from "react-icons/md";

export default function FavoriteButton({ subscription }: { subscription?: Subscription }) {
    const favoriteLabel = subscription?.favorite ? 'Remove from favorites' : 'Add to favorites';
    const favoriteIcon = subscription?.favorite ? <MdStar /> : <MdStarOutline />;
    const favoriteColor = subscription?.favorite ? 'yellow.400' : 'gray.400';
    const favoriteBg = useColorModeValue('gray.100', 'gray.700');
    return (
        <Tooltip label={favoriteLabel} hasArrow>
            <IconButton
                aria-label={favoriteLabel}
                icon={favoriteIcon}
                variant="ghost"
                textColor={favoriteColor}
                sx={{
                _hover: {
                    bg: favoriteBg,
                },
                }} />
        </Tooltip>
    )
}