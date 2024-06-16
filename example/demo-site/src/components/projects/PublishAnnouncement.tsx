import { Alert, AlertIcon, Box, Container, Stack, VStack, useToast } from '@chakra-ui/react'
import { Account } from '../../lib/ryp-subscription-api';
import ProjectsHeader from './ProjectsHeader';
import { useRouter } from 'next/router';
import PublishAnnouncementForm from './PublishAnnouncementForm';
import { useState } from 'react';
import { useApi } from '@/contexts/ApiProvider';
import useTranslation from 'next-translate/useTranslation';

type PublishAnnouncementProps = {
    account: Account;
};

export interface AnnouncementFormData {
    title: string;
    content: string;
    link?: string;
}

const defaultFormData: AnnouncementFormData = {
    title: '',
    content: '',
    link: ''
};

interface AnnouncementFormProps {
    formData: AnnouncementFormData;
    onFormChange: (field: keyof AnnouncementFormData, value: string) => void;
}

export default function PublishAnnouncement({ account }: PublishAnnouncementProps) {
    const router = useRouter();
    const api = useApi();
    const [publishSuccess, setPublishSuccess] = useState(false);
    const toast = useToast();
    const { t } = useTranslation('publish');
    const projectId = router.query.projectid as string;

    const [formData, setFormData] = useState(defaultFormData);

    const handleFormChange = (field: keyof AnnouncementFormData, value: string): void => {
        setFormData((prev) => ({
        ...prev,
        [field]: value,
        }));
    };

    const publishAnnouncement = async () => {
        try {
            await api.publishAnnouncement(projectId, formData);
            setPublishSuccess(true);
        } catch (error: any) {
            if (error?.response.status === 403) {
                toast({
                    title: t('publishError'),
                    description: error.response.data.messages.map((messageContent: any) => messageContent.message).join('\n'),
                    status: 'error',
                    duration: 15000,
                    isClosable: true,
                });
            }
        }
    }

    return (
        <Box
            maxW="7xl"
            mx="auto"
            px={{ base: '4', md: '8', lg: '12' }}
            py={{ base: '6', md: '8', lg: '12' }}
        >
            <ProjectsHeader
                backButtonLink='/publish'
                backButtonText={t('backToProjectList')}
                title={t('publishAnnouncementTitle')}
                description={t('publishAnnouncementDescription')}
            />
            <Container py={{ base: '4', md: '8' }}>
                <VStack spacing="0">
                    <Stack spacing="4" direction={{ base: 'row', md: 'column' }} w="full">
                        {!publishSuccess && <PublishAnnouncementForm formData={formData} onFormChange={handleFormChange} onSubmit={publishAnnouncement} />}
                        {publishSuccess && (<Alert status="success">
                            <AlertIcon />
                            {t('publishAnnouncementSuccess')}
                        </Alert>)}
                    </Stack>
                </VStack>
            </Container>
        </Box>
    )
}