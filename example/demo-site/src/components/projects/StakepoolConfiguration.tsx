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
    HStack,
    Icon,
    Input,
    InputGroup,
    InputLeftAddon,
    Spinner,
    Stack,
    StackDivider,
    Step,
    StepDescription,
    StepIcon,
    StepIndicator,
    StepNumber,
    Stepper,
    StepSeparator,
    StepStatus,
    StepTitle,
    Text,
    Textarea,
    useToast,
    useColorModeValue as mode,
} from '@chakra-ui/react'
import Dropzone from '../Dropzone'
import { ProjectConfigurationProps } from './ProjectConfiguration'
import { useEffect, useRef, useState } from 'react';
import useTranslation from 'next-translate/useTranslation';
import { MdCheck, MdContentCopy, MdOutlineTimer, MdRefresh } from 'react-icons/md';
import { Controller, useForm } from 'react-hook-form';
import { ProjectData } from './NewProject';
import { useApi } from '@/contexts/ApiProvider';
import { StakepoolVerification } from '@/lib/ryp-verification-api';
import { Timer } from '../timer/Timer';
import NextLink from '../NextLink';

const poolHashRegex = /^[a-f0-9]{56}$/i;
const CIP_0022_DOMAIN = 'ryp.io';

export default function StakepoolConfiguration({ type, onSubmit }: ProjectConfigurationProps) {
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
    const [logoErrors, setLogoErrors] = useState<string[]>([]);
    const [selectedLogo, setSelectedLogo] = useState<File | null>(null);
    const [ticker, setTicker] = useState<string>('');
    const [urlPrefix, setUrlPrefix] = useState<string>('https://');
    const [poolHash, setPoolHash] = useState('');
    const [expired, setExpired] = useState<boolean>(false);
    const [expirationTime, setExpirationTime] = useState<string>(new Date().toISOString());
    const [stakepoolVerification, setStakepoolVerification] = useState<StakepoolVerification | null>(null);
    const [verificationLoading, setVerificationLoading] = useState<boolean>(false);
    const [currentVerificationStep, setCurrentVerificationStep] = useState<number>(0);
    const [cncliCommand, setCncliCommand] = useState<string>('');
    const [challengeError, setChallengeError] = useState<string>('');
    const [vrfVerificationKey, setVrfVerificationKey] = useState<any>(null);
    const [vrfVkeyError, setVrfKeyError] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
    const api = useApi();
    const toast = useToast();
    const poolHashRef = useRef<HTMLTextAreaElement | null>(null);
    const { t } = useTranslation('publish');
    const {
        register,
        handleSubmit,
        formState: { errors },
        control,
        setValue,
        watch,
    } = useForm<ProjectData>();
    const { ref } = register(`stakepool.poolHash`, { required: true, pattern: /^[A-Fa-f0-9]{56}$/i });

    useEffect(() => {
        poolHashRef.current?.focus();
    }, [type]);

    const onFileSelected = (file: File) => {
        setSelectedLogo(file);
        const fileUrl = URL.createObjectURL(file);
        setAvatarUrl(fileUrl);
        if (poolHash !== '' && stakepoolVerification === null) {
            startStakepoolVerification(poolHash);
        }
    };

    const finalizeSubmit = (data: ProjectData) => {
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
        onSubmit(data);
    }

    const poolHashChanged = async (event: React.ChangeEvent<HTMLTextAreaElement>, onChange: (...event: any[]) => void) => {
        onChange(event);
        resetVerification();
        const newPoolHash = event.target.value;
        if (poolHashRegex.test(newPoolHash)) {
            try {
                const poolDetails = await api.getStakepoolDetails(newPoolHash);
                setPoolHash(newPoolHash);
                setTicker(poolDetails.ticker);
                setValue('description', poolDetails.description);
                if (poolDetails.homepage.startsWith('http://')) {
                    setUrlPrefix('http://');
                } else {
                    setUrlPrefix('https://');
                }
                setValue('url', poolDetails.homepage.split('://')[1]);
                setValue('name', poolDetails.name);
                if (selectedLogo !== null) {
                    startStakepoolVerification(newPoolHash);
                }
                return;
            } catch (e) {
            }
        }
        setTicker('');
        setValue('description', '');
        setValue('url', '');
        setValue('name', '');
    }

    function startStakepoolVerification(verifyPoolHash: string) {
        setVerificationLoading(true);
        api.startStakepoolVerification(verifyPoolHash).then((stakepoolVerification) => {
            setStakepoolVerification(stakepoolVerification);
            setExpired(false);
            setExpirationTime(stakepoolVerification.expirationTime);
            setCncliCommand(t('add.form.stakepools.cncliCommand', { domain: CIP_0022_DOMAIN, nonce: stakepoolVerification.nonce }));
        }).finally(() => {
            setVerificationLoading(false);
        });
    }
    
    function restartVerification() {
        resetVerification();
        startStakepoolVerification(poolHash);
    }

    function resetVerification() {
        setCurrentVerificationStep(0);
        setStakepoolVerification(null);
        setCncliCommand('');
        setChallengeError('');
        setVrfKeyError('');
    }
    
    const copyCncliToClipboard = () => {
        if (stakepoolVerification !== null) {
            setCurrentVerificationStep(1);
            navigator.clipboard.writeText(t('add.form.stakepools.cncliCommand', { domain: CIP_0022_DOMAIN, nonce: stakepoolVerification.nonce })).then(() => {
                toast({
                    description: t('add.form.stakepools.cncliCopiedToClipboard'),
                    status: 'success',
                    duration: 5000,
                    isClosable: true,
                });
            });
        }
    };

    const onVrfVkeySelected = async (file: File) => {
        const fileText = await file.text();
        setCurrentVerificationStep(1);
        try {
            const fileContentJson = JSON.parse(fileText);
            if (fileContentJson.type === 'VrfVerificationKey_PraosVRF' && stakepoolVerification) {
                setVrfKeyError('');
                setVrfVerificationKey(fileContentJson);
                return;
            }
        } catch (e) {
            // Ignore the specific error, we cannot cover all cases anyway
        }
        setVrfKeyError(t('add.form.stakepools.vrfVkeyError'));
    };

    const submitVerification = () => {
        if (stakepoolVerification === null) {
            return;
        }
        const signature = watch(`stakepool.verification.signature`);
        const completedVerification = { ...stakepoolVerification, signature, vrfVerificationKey };
        api.testStakepoolVerification(stakepoolVerification.poolHash, completedVerification).then(async (verificationResult) => {
            if (verificationResult) {
                setValue(`stakepool.verification`, verificationResult);
                // Immediately initiate the creation, so there is no additional click needed. If it fails, the button to click is still available
                try {
                    await handleSubmit(finalizeSubmit)();
                } catch (e) {
                    toast({
                        title: t('add.form.stakepools.verificationFailed'),
                        status: 'error',
                        isClosable: true,
                    });
                }
                setCurrentVerificationStep(3);
            } else {
                setChallengeError(t('add.form.stakepools.challengeSignatureError'));
            }
        });
    }

    useEffect(() => {
        const expirationDate = new Date(expirationTime);
        const currentTime = new Date();
        const timeToExpiration = expirationDate.getTime() - currentTime.getTime() + 1000;
    
        let timerId: NodeJS.Timeout | null = null;
    
        if (timeToExpiration > 0) {
          timerId = setTimeout(() => {
            setExpired(true);
          }, timeToExpiration);
        } else {
          setExpired(false);
        }
    
        return () => {
          if (timerId) {
            clearTimeout(timerId);
          }
        };
      }, [expirationTime, setExpired]);

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
                <FormControl id="poolHash" isRequired isInvalid={!!errors.stakepool?.poolHash}>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Box w='md'>
                            <FormLabel variant="inline">{t('add.form.stakepools.poolHash')}</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                {t('add.form.stakepools.poolHashHelper')}
                            </FormHelperText>
                        </Box>
                        <Stack w="100%">
                            <Controller
                                name={`stakepool.poolHash`}
                                control={control}
                                render={({ field }) => (
                                    <Textarea rows={2} resize="none"
                                        maxW={{ md: '3xl' }}
                                        {...field}
                                        onChange={(event) => poolHashChanged(event, field.onChange)}
                                        ref={(e) => {
                                            ref(e);
                                            poolHashRef.current = e;
                                        }}
                                    />)}
                            />
                            <FormErrorMessage>{!!errors.stakepool?.poolHash && t('add.form.stakepools.poolHashFormatError')}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="name" isRequired isReadOnly>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Box>
                            <FormLabel variant="inline">{t('add.form.stakepools.name')}</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                {t('add.form.automaticallyFilledOut')}
                            </FormHelperText>
                        </Box>
                        <Stack w="100%">
                            <Input maxW={{ md: '3xl' }}
                                variant='unstyled'
                                {...register('name', { required: true })}
                            />
                            <FormErrorMessage>{errors.name && t('add.form.nameError')}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="ticker" isRequired isReadOnly>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Box>
                            <FormLabel variant="inline">{t('add.form.stakepools.ticker')}</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                {t('add.form.automaticallyFilledOut')}
                            </FormHelperText>
                        </Box>
                        <Stack w="100%">
                            <Input maxW={{ md: '3xl' }}
                                variant='unstyled'
                                value={ticker}
                            />
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="website" isRequired isReadOnly>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Box>
                            <FormLabel variant="inline">{t('add.form.website')}</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                {t('add.form.automaticallyFilledOut')}
                            </FormHelperText>
                        </Box>
                        <Stack w="100%">
                            <InputGroup maxW={{ md: '3xl' }}>
                                <InputLeftAddon>{urlPrefix}</InputLeftAddon>
                                <Input
                                    {...register('url', { required: true })}
                                />
                            </InputGroup>
                            <FormErrorMessage>{errors.url && t('add.form.urlError')}</FormErrorMessage>
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="description" isRequired isReadOnly>
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
                            <Textarea maxW={{ md: '3xl' }} rows={5} resize="none" variant='unstyled'
                                {...register('description', { required: true })}
                            />
                            <FormErrorMessage>{errors.description && t('add.form.descriptionError')}</FormErrorMessage>
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
                {stakepoolVerification !== null && (<FormControl id="verificationNonce" isReadOnly>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <Stack spacing={{ base: '2' }} w='md'>
                            <Box>
                                <FormLabel variant="inline">{t('add.form.stakepools.verificationNonce')}</FormLabel>
                                <FormHelperText mt="0" color="fg.muted" mb={4}>
                                    {t('add.form.stakepools.challengeHelper')}
                                </FormHelperText>
                                <HStack spacing={6} hidden={expired}>
                                    <Icon as={MdOutlineTimer} color={mode('brand.500', 'brand.200')} boxSize="2em" />
                                    <Timer expiresInSeconds={new Date(expirationTime).getTime()} isIncludeDays={false} isIncludeHours={false} />
                                </HStack>
                                <Button hidden={!expired} onClick={restartVerification} leftIcon={<MdRefresh />}>{t('add.form.stakepools.restartVerification')}</Button>
                            </Box>
                            <Box>
                                <NextLink href='https://cips.cardano.org/cip/CIP-0022' isExternal>{t('add.form.stakepools.learnMore')}</NextLink>
                            </Box>
                        </Stack>
                        {verificationLoading && <Box><Spinner /></Box>}
                        {!verificationLoading && <Textarea maxW={{ md: '3xl' }} rows={2} resize="none" variant='unstyled' readOnly value={stakepoolVerification.nonce ?? ''} />}
                    </Stack>
                </FormControl>)}
                {cncliCommand.length > 0 && (<Box maxW="3xl">
                    <Text textStyle="lg" fontWeight="medium">
                        {t('add.form.stakepools.stakepoolVerification')}
                    </Text>
                    <Stepper index={currentVerificationStep} orientation='vertical' my={4} colorScheme='brand'>
                        <Step>
                            <StepIndicator>
                                <StepStatus active={<StepNumber />} complete={<StepNumber />} />
                            </StepIndicator>
                            <Box ml={4}>
                                <StepTitle>{t('add.form.stakepools.step1Title')}</StepTitle>
                                <StepDescription>{t('add.form.stakepools.step1Description')}</StepDescription>
                                <Stack
                                    direction={{ base: 'column' }}
                                    my={4}   
                                >
                                    <Textarea rows={3} resize="none" variant='unstyled' readOnly value={cncliCommand} />
                                    <Button alignSelf={{ sm: 'flex-end' }} onClick={copyCncliToClipboard} variant={currentVerificationStep === 0 ? 'primary' : 'outline'}><MdContentCopy /></Button>
                                </Stack>
                            </Box>
                            <StepSeparator />
                        </Step>
                        {currentVerificationStep > 0 && (<Step>
                            <StepIndicator>
                                <StepStatus active={<StepNumber />} incomplete={<StepNumber />} complete={<StepNumber />} />
                            </StepIndicator>
                            <Box ml={4}>
                                <StepTitle>{t('add.form.stakepools.step2Title')}</StepTitle>
                                <StepDescription>{t('add.form.stakepools.step2Description')}</StepDescription>
                                <Stack
                                    direction={{ base: 'column' }}
                                    my={4}   
                                >
                                    <FormControl id="challengeSignature" isRequired isInvalid={challengeError.length > 0}>
                                        <FormLabel variant="inline">{t('add.form.stakepools.challengeSignature')}</FormLabel>
                                        <FormHelperText mt="0" color="fg.muted">
                                            {t('add.form.stakepools.challengeSignatureHelper')}
                                        </FormHelperText>
                                        <Textarea
                                            rows={3}
                                            resize="none"
                                            maxW={{ md: '3xl' }}
                                            placeholder={'0'.repeat(160)}
                                            {...register(`stakepool.verification.signature`, { required: true, pattern: /^[0-9a-f]{160}$/i })}
                                        />
                                        <FormErrorMessage>{challengeError.length && challengeError}</FormErrorMessage>
                                    </FormControl>
                                    <FormControl id="vrfVkey" isRequired isInvalid={vrfVkeyError.length > 0}>
                                        <FormLabel variant="inline" mt={4}>{t('add.form.stakepools.vrfVKey')}</FormLabel>
                                        <FormHelperText mt="0" color="fg.muted">
                                            {t('add.form.stakepools.vrfVkeyHelper')}
                                        </FormHelperText>
                                        <Dropzone width="full" guidance={t('add.form.stakepools.vrfVkeyGuidance')} onChangeFile={onVrfVkeySelected} />
                                        <FormErrorMessage>{vrfVkeyError.length && vrfVkeyError}</FormErrorMessage>
                                    </FormControl>
                                    <Button alignSelf={{ sm: 'flex-end' }} onClick={submitVerification} variant={currentVerificationStep === 1 ? 'primary' : 'outline'}>{t('add.form.stakepools.submitVerification')}</Button>
                                </Stack>
                            </Box>
                            <StepSeparator />
                        </Step>)}
                        {currentVerificationStep > 1 && (<Step>
                            <StepIndicator>
                                <StepStatus active={<StepNumber />} incomplete={<StepNumber />} complete={<StepIcon />} />
                            </StepIndicator>
                            <Box ml={4}>
                                <StepTitle>{t('add.form.stakepools.step3Title')}</StepTitle>
                                <HStack>
                                    <MdCheck size="3em" />
                                    <StepDescription>{t('add.form.stakepools.step3Description')}</StepDescription>
                                </HStack>
                            </Box>
                        </Step>)}
                    </Stepper>

                </Box>)}

                <Flex direction="row-reverse">
                    {currentVerificationStep >= 2 && (<Button isLoading={submitting} onClick={handleSubmit(finalizeSubmit)}>{t('add.form.createStakepoolProject')}</Button>)}
                </Flex>
            </Stack>
        </Stack>
    </Container>)
}