import {
    Avatar,
    Box,
    Button,
    Container,
    Divider,
    Flex,
    FormControl,
    FormHelperText,
    FormLabel,
    Input,
    InputGroup,
    InputLeftAddon,
    Stack,
    StackDivider,
    Text,
    Textarea,
} from '@chakra-ui/react'
import Dropzone from '../Dropzone'
import { ProjectConfigurationProps } from './ProjectConfiguration'
import { useEffect, useRef, useState } from 'react';
import useTranslation from 'next-translate/useTranslation';

export default function NFTConfiguration({ type, formData, onFormChange, onSubmit }: ProjectConfigurationProps) {
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
    const nameRef = useRef<HTMLInputElement>(null);
    const { t } = useTranslation('projects');

    useEffect(() => {
        nameRef.current?.focus();
    }, [type]);

    
    const onFileSelected = (file: Blob | MediaSource) => {
        const fileUrl = URL.createObjectURL(file);
        setAvatarUrl(fileUrl);
        onFormChange('logo', fileUrl);
    };

    return (<Container py={{ base: '4', md: '8' }}>
        <Stack spacing="5">
            <Stack spacing="4" direction={{ base: 'column', sm: 'row' }} justify="space-between">
                <Box>
                    <Text textStyle="lg" fontWeight="medium">
                        {t('add.form.title')}
                    </Text>
                    <Text color="fg.muted" textStyle="sm">
                        {t('add.form.cta')}
                    </Text>
                </Box>
            </Stack>
            <Divider />
            <Stack spacing="5" divider={<StackDivider />}>
                <FormControl id="name" isRequired>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">{t('add.form.name')}</FormLabel>
                        <Input maxW={{ md: '3xl' }} value={formData.name} ref={nameRef}
                            onChange={(e) => onFormChange('name', e.target.value) } />
                    </Stack>
                </FormControl>
                <FormControl id="logo" isRequired>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">{t('add.form.logo')}</FormLabel>
                        <Stack
                            spacing={{ base: '3', md: '5' }}
                            direction={{ base: 'column', sm: 'row' }}
                            width="full"
                            maxW={{ md: '3xl' }}
                        >
                            {avatarUrl && <Avatar size="lg" src={avatarUrl} />}
                            <Dropzone width="full" guidance={t('add.form.logoFileGuidance')} onChangeFile={onFileSelected} />
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="website" isRequired>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">{t('add.form.website')}</FormLabel>
                        <InputGroup maxW={{ md: '3xl' }}>
                            <InputLeftAddon>https://</InputLeftAddon>
                            <Input value={formData.url}
                              onChange={(e) => onFormChange('url', e.target.value) } />
                        </InputGroup>
                    </Stack>
                </FormControl>
                <FormControl id="description" isRequired>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Box>
                            <FormLabel variant="inline">{t('add.form.description')}</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                {t('add.form.descriptionHelper')}
                            </FormHelperText>
                        </Box>
                        <Textarea maxW={{ md: '3xl' }} rows={5} resize="none" value={formData.description}
                            onChange={(e) => onFormChange('description', e.target.value) } />
                    </Stack>
                </FormControl>
                <FormControl id="policyid" isRequired>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Box>
                            <FormLabel variant="inline">{t('add.form.logo')}</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                {t('add.form.policyIdHelper')}
                            </FormHelperText>
                        </Box>
                        <Input maxW={{ md: '3xl' }} value={formData.policy}
                            onChange={(e) => onFormChange('policy', e.target.value) }
                        />
                    </Stack>
                </FormControl>

                <Flex direction="row-reverse">
                    <Button onClick={onSubmit}>{t('add.form.createNftProject')}</Button>
                </Flex>
            </Stack>
        </Stack>
    </Container>)
}