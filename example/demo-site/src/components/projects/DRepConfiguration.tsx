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
    Img,
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
import { useForm } from 'react-hook-form';
import { ProjectData } from './NewProject';
import { BrowserWallet, Wallet } from '@meshsdk/core';
import { useApi } from '@/contexts/ApiProvider';

export default function DRepConfiguration({ type, formData, onSubmit }: ProjectConfigurationProps) {
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
    const [logoErrors, setLogoErrors] = useState<string[]>([]);
    const [selectedLogo, setSelectedLogo] = useState<File | null>(null);
    const nameRef = useRef<HTMLInputElement | null>(null);
    const { t } = useTranslation('publish');
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<ProjectData>();
    const { ref, ...nameRest } = register('name', { required: true });
    const api = useApi();

    const [wallets, setWallets] = useState<Wallet[]>([]);
  const [selectWallet, setSelectWallet] = useState(false);

  useEffect(() => {
    setWallets(BrowserWallet.getInstalledWallets());
  }, []);

    useEffect(() => {
        nameRef.current?.focus();
    }, [type]);
    
    const onFileSelected = (file: File) => {
        setSelectedLogo(file);
        const fileUrl = URL.createObjectURL(file);
        setAvatarUrl(fileUrl);
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
        onSubmit(data);
    }

    const verifyDrep = async (walletName: string) => {
        // const activeWallet = await BrowserWallet.enable(walletName);
        // const wapi = await window.cardano.nufi.enable({ extensions: [{ cip : 95 }]})
        // const rewardAddresses = await activeWallet.getRewardAddresses();
        // const stakeAddress = rewardAddresses[0];
        // const addresses = await activeWallet.getUsedAddresses();
        // // const nonceResponse = await api.createNonce(addresses[0], stakeAddress);
        // const nonceResponse = '74657374'
        // const pubDrepKeyID = await wapi.cip95.getPubDRepKey()
        // const pubDrepID = '4519F294D80B0FCC6697BDE8F36629BE8EBF9527BE023FE73673F1A9'.toLowerCase();
        // console.log(pubDrepKeyID);
        // console.log(pubDrepID);
        // console.log(await wapi.cip95.signData(pubDrepID, nonceResponse));

    }

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

                <Stack spacing="3">
                    {wallets.map((wallet) => (
                    <Button key={wallet.name}
                        variant="secondary"
                        cursor="pointer"
                        leftIcon={<Img src={wallet.icon} alt={wallet.name} h='1.5em' w='1.5em' />}
                        onClick={() => {
                            verifyDrep(wallet.name);
                        }}
                    >
                        {wallet.name}
                    </Button>)
                    )}
                </Stack>

                <Flex direction="row-reverse">
                    <Button onClick={handleSubmit(finalizeSubmit)}>{t('add.form.createTokenBasedProject')}</Button>
                </Flex>
            </Stack>
        </Stack>
    </Container>)
}