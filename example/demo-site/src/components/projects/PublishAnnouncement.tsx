import { Box, Container, Stack, VStack } from '@chakra-ui/react'
import { Account } from '@/lib/ryp-api';
import ProjectsHeader from './ProjectsHeader';
import { useRouter } from 'next/router';
import PublishAnnouncementForm from './PublishAnnouncementForm';
import { useState } from 'react';

type PublishAnnouncementProps = {
    account: Account;
};

export interface AnnouncementFormData {
    title: string;
    content: string;
    link?: string; // Optional link
}

const defaultFormData: AnnouncementFormData = {
    title: '',
    content: '',
    link: ''
};

// Props type for the AnnouncementForm component
interface AnnouncementFormProps {
    formData: AnnouncementFormData;
    onFormChange: (field: keyof AnnouncementFormData, value: string) => void;
}

export default function PublishAnnouncement({ account }: PublishAnnouncementProps) {
    const router = useRouter();
    const projectId = router.query.projectid as string;

    const [formData, setFormData] = useState(defaultFormData);

    const handleFormChange = (field: keyof AnnouncementFormData, value: string): void => {
        setFormData((prev) => ({
        ...prev,
        [field]: value,
        }));
    };

    return (
        <Box
            maxW="7xl"
            mx="auto"
            px={{ base: '4', md: '8', lg: '12' }}
            py={{ base: '6', md: '8', lg: '12' }}
        >
            <ProjectsHeader
                backButtonLink='/projects'
                backButtonText="Back to list"
                title="Publish announcement"
                description="Give us your announcement details to publish it."
            />
            <Container py={{ base: '4', md: '8' }}>
                <VStack spacing="0">
                    <Stack spacing="4" direction={{ base: 'row', md: 'column' }} minW="3xl">
                        <PublishAnnouncementForm formData={formData} onFormChange={handleFormChange} />
                    </Stack>
                </VStack>
            </Container>
        </Box>
    )
}