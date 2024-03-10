import { Box,  Stack, Text, Button, HStack } from '@chakra-ui/react'
import NextLink from '../NextLink';
import { FaArrowLeft } from 'react-icons/fa';

type ProjectsHeaderProps = {
    backButtonLink?: string;
    backButtonText?: string;
    title: string;
    description: string;
    children?: React.ReactNode;
}

export default function ProjectsHeader({ backButtonLink, backButtonText, title, description, children }: ProjectsHeaderProps) {
    return (<Box as="section" pb={{ base: '4', md: '8' }}>
        <Box bg="bg.surface" px={{ base: '4', md: '6' }} py="5" boxShadow="sm" borderRadius="lg">
            <Stack direction={{ base: 'column', sm: 'row' }} spacing="4" justify="start" alignItems="center">
                {backButtonLink && (<NextLink href={backButtonLink}>
                    <Button variant="ghost" size="lg">
                        <HStack><FaArrowLeft /><Text>{backButtonText}</Text></HStack>
                    </Button>
                </NextLink>)}
                <Box flexGrow="1">
                    <Stack spacing="1">
                        <Text textStyle="lg" fontWeight="medium">
                            {title}
                        </Text>
                        <Text textStyle="sm" color="fg.muted">
                            {description}
                        </Text>
                    </Stack>
                </Box>
                {children}
            </Stack>
        </Box>
    </Box>);
}