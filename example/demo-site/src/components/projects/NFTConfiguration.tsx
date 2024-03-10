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

export default function NFTConfiguration({ type, formData, onFormChange, onSubmit }: ProjectConfigurationProps) {
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
    const nameRef = useRef<HTMLInputElement>(null);

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
                        Your Project
                    </Text>
                    <Text color="fg.muted" textStyle="sm">
                        Let us know your project details
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
                        <FormLabel variant="inline">Project Name</FormLabel>
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
                        <FormLabel variant="inline">Logo</FormLabel>
                        <Stack
                            spacing={{ base: '3', md: '5' }}
                            direction={{ base: 'column', sm: 'row' }}
                            width="full"
                            maxW={{ md: '3xl' }}
                        >
                            {avatarUrl && <Avatar size="lg" src={avatarUrl} />}
                            <Dropzone width="full" guidance="PNG, JPG or GIF up to 2MB" onChangeFile={onFileSelected} />
                        </Stack>
                    </Stack>
                </FormControl>
                <FormControl id="website" isRequired>
                    <Stack
                        direction={{ base: 'column', md: 'row' }}
                        spacing={{ base: '1.5', md: '8' }}
                        justify="space-between"
                    >
                        <FormLabel variant="inline">Website</FormLabel>
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
                            <FormLabel variant="inline">Description</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                Write a short introduction for your project
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
                            <FormLabel variant="inline">Policy ID</FormLabel>
                            <FormHelperText mt="0" color="fg.muted">
                                The primary Cardano policy ID for your project
                            </FormHelperText>
                        </Box>
                        <Input maxW={{ md: '3xl' }} value={formData.policy}
                            onChange={(e) => onFormChange('policy', e.target.value) }
                        />
                    </Stack>
                </FormControl>

                <Flex direction="row-reverse">
                    <Button onClick={onSubmit}>Create NFT project</Button>
                </Flex>
            </Stack>
        </Stack>
    </Container>)
}