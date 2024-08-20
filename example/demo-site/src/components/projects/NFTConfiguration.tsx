import {
    Avatar,
    Box,
    Button,
    Container,
    Divider,
    Flex,
    FormControl,
    FormErrorMessage,
    FormHelperText,
    FormLabel,
    IconButton,
    Input,
    InputGroup,
    InputLeftAddon,
    Stack,
    StackDivider,
    Text,
    Textarea,
    VStack,
} from '@chakra-ui/react'
import Dropzone from '../Dropzone'
import { ProjectConfigurationProps } from './ProjectConfiguration'
import { useEffect, useRef, useState } from 'react';
import useTranslation from 'next-translate/useTranslation';
import { MdAdd, MdRemove } from 'react-icons/md';
import { TokenPolicy } from '@vibrantnet/core';
import { nanoid } from 'nanoid';
import { FieldErrors, UseFormRegister, useForm } from 'react-hook-form';
import { ProjectData } from './NewProject';

interface TokenPolicyWithId extends TokenPolicy {
    id: string;
}

type PolicyInputFieldsProps = {
    policy: TokenPolicyWithId;
    removePolicyWithId: (id: string) => void;
    canRemove: boolean;
    errors: FieldErrors<ProjectData>;
    register: UseFormRegister<ProjectData>;
}

const PolicyInputFields = ({ policy, removePolicyWithId, canRemove, errors, register }: PolicyInputFieldsProps) => {
    const { t } = useTranslation('publish');
    const hasNameError = !!errors.policies?.[policy.id as any]?.projectName;
    const hasIdError = !!errors.policies?.[policy.id as any]?.policyId;
    return (
        <VStack spacing={2} w='100%' alignItems='start'>
            <FormControl id={`policy-${policy.id}-name`} isRequired isInvalid={hasNameError}>
                <Flex w='100%' gap={2}>
                    <Input
                        flexGrow='1'
                        placeholder={t('add.form.policyReadableName')}
                        isInvalid={hasNameError}
                        {...register(`policies.${policy.id}.projectName`, { required: true }) }
                    />
                    {canRemove && (<IconButton aria-label={t('add.form.removePolicy')} onClick={() => removePolicyWithId(policy.id)} icon={<MdRemove />} variant='outline' />)}
                </Flex>
                <FormErrorMessage>{t('add.form.policyNameError')}</FormErrorMessage>
            </FormControl>
            <FormControl id={`policy-${policy.id}-id`} isRequired isInvalid={hasIdError}>
                <Input
                    maxW={{ md: '3xl' }}
                    placeholder={t('add.form.policyId')}
                    isInvalid={hasIdError}
                    {...register(`policies.${policy.id}.policyId`, { required: true, pattern: /^[A-Fa-f0-9]{56}$/i }) }
                />
                <FormErrorMessage>{t('add.form.policyIdError')}</FormErrorMessage>
            </FormControl>
        </VStack>
    );
}

export default function NFTConfiguration({ type, formData, onSubmit }: ProjectConfigurationProps) {
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
    const [policies, setPolicies] = useState<Record<string, TokenPolicyWithId>>(formData.policies as Record<string, TokenPolicyWithId>);
    const [logoErrors, setLogoErrors] = useState<string[]>([]);
    const [selectedLogo, setSelectedLogo] = useState<File | null>(null);
    const [submitting, setSubmitting] = useState<boolean>(false);
    const nameRef = useRef<HTMLInputElement | null>(null);
    const { t } = useTranslation('publish');
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<ProjectData>();
    const { ref, ...nameRest } = register('name', { required: true });

    useEffect(() => {
        nameRef.current?.focus();
    }, [type]);
    
    const onFileSelected = (file: File) => {
        setSelectedLogo(file);
        const fileUrl = URL.createObjectURL(file);
        setAvatarUrl(fileUrl);
    };

    const finalizeSubmit = async (data: ProjectData) => {
        if (!selectedLogo) {
            setLogoErrors(['add.form.logoRequired']);
            return;
        } else {
            const allowedTypes = ['image/png', 'image/jpeg', 'image/webp', 'image/svg+xml'];
            if (!allowedTypes.includes(selectedLogo.type)) {
                setLogoErrors(['add.form.logoFileTypesError']);
                return;
            }
        }
        data.logo = selectedLogo;
        setSubmitting(true);
        try {
            await onSubmit(data);
        } finally {
            setSubmitting(false);
        }
    }

    const removePolicyWithId = (id: string) => {
        const newPolicies = { ...policies };
        delete newPolicies[id];
        setPolicies(newPolicies);
    };

    const addEmptyPolicy = () => {
        const newId = nanoid();
        const newPolicies = { ...policies };
        newPolicies[newId] = { projectName: '', policyId: '', id: newId };
        setPolicies(newPolicies);
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
                <FormControl id="name" isRequired isInvalid={!!errors.name}>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">{t('add.form.name')}</FormLabel>
                        <Stack w="100%">
                            <Input maxW={{ md: '3xl' }}
                                {...nameRest}
                                ref={(e) => {
                                    ref(e);
                                    nameRef.current = e;
                                }}
                            />
                            <FormErrorMessage>{errors.name && t('add.form.nameError')}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="logo" isRequired isInvalid={logoErrors.length > 0}>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">{t('add.form.logo')}</FormLabel>
                        <Stack w="100%">
                            <Stack
                                spacing={{ base: '3', md: '5' }}
                                direction={{ base: 'column', sm: 'row' }}
                                width="full"
                                maxW={{ md: '3xl' }}
                            >
                                {avatarUrl && <Avatar size="lg" src={avatarUrl} />}
                                <Dropzone width="full" guidance={t('add.form.logoFileGuidance')} onChangeFile={onFileSelected} />
                            </Stack>
                            <FormErrorMessage>{logoErrors.length && (logoErrors.map((logoError) => t(logoError)))}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="website" isRequired isInvalid={!!errors.url}>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">{t('add.form.website')}</FormLabel>
                        <Stack w="100%">
                            <InputGroup maxW={{ md: '3xl' }}>
                                <InputLeftAddon>https://</InputLeftAddon>
                                <Input
                                    {...register('url', { required: true })}
                                />
                            </InputGroup>
                            <FormErrorMessage>{errors.url && t('add.form.urlError')}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="description" isRequired isInvalid={!!errors.description}>
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
                        <Stack w="100%">
                            <Textarea maxW={{ md: '3xl' }} rows={5} resize="none"
                                {...register('description', { required: true })}
                            />
                            <FormErrorMessage>{errors.description && t('add.form.descriptionError')}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <Stack
                    direction={{ base: 'column', md: 'row' }}
                    spacing={{ base: '1.5', md: '8' }}
                    justify="space-between"
                >
                    <Stack spacing={{ base: '1.5', md: '8' }} w='md'>
                        <Box>
                            <FormControl id="policyid" isRequired>
                                <FormLabel variant="inline">{t('add.form.policyIds')}</FormLabel>
                                <FormHelperText mt="0" color="fg.muted">
                                    {t('add.form.policyIdHelper')}
                                </FormHelperText>
                            </FormControl>
                        </Box>
                        <Button aria-label={t('add.form.addPolicy')} onClick={() => addEmptyPolicy()} leftIcon={<MdAdd />} variant='outline' tabIndex={200}>
                            {t('add.form.addPolicy')}
                        </Button>
                    </Stack>
                    <VStack maxW={{ md: '3xl' }} w='100%' alignItems='start'>
                        {Object.entries(policies).map(([key, policy]) => (
                            <PolicyInputFields
                                key={key}
                                policy={policy}
                                removePolicyWithId={removePolicyWithId}
                                canRemove={Object.values(policies).length > 1}
                                errors={errors}
                                register={register}
                            />
                        ))}
                    </VStack>
                </Stack>
                

                <Flex direction="row-reverse">
                    <Button onClick={handleSubmit(finalizeSubmit)} isLoading={submitting}>{t('add.form.createTokenBasedProject')}</Button>
                </Flex>
            </Stack>
        </Stack>
    </Container>)
}