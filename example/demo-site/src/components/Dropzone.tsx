import {
    Button, Center, CenterProps, HStack, Icon, Square, Text, VStack,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import { useRef, useState } from 'react';
import { FiUploadCloud } from 'react-icons/fi';

interface DropzoneProps extends CenterProps {
    guidance: string
    onChangeFile(file: File): void
}

export default function Dropzone({ guidance, onChangeFile, ...rest }: DropzoneProps) {
    const [file, setFile] = useState<File | null>(null);
    const [isDragOver, setIsDragOver] = useState(false);
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const { t } = useTranslation('common');

    const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const selectedFile = e.target.files && e.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);
            onChangeFile(selectedFile);
        }
    };

    const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        const droppedFile = e.dataTransfer.files && e.dataTransfer.files[0];
        if (droppedFile) {
            setFile(droppedFile);
            setIsDragOver(false);
        }
    };

    const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragOver(true);
    };

    const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragOver(false);
    };

    return (
        <Center
            borderWidth="1px"
            borderRadius="lg"
            px="6"
            py="4"
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            backgroundColor={isDragOver ? 'gray.500' : 'bg.surface'}
            transition="background-color 0.3s ease"
            {...rest}
        >
            <VStack spacing="3">
                {file ? (
                    <>
                        <Square size="10" bg="bg.subtle" borderRadius="lg">
                            <Icon as={FiUploadCloud} boxSize="5" color="fg.muted" />
                        </Square>
                        <VStack spacing="1">
                            <Text textStyle="sm" color="fg.muted">
                                {file.name}
                            </Text>
                            <Button
                                variant="text"
                                colorScheme="blue"
                                size="sm"
                                onClick={() => {
                                    if (fileInputRef.current) {
                                        fileInputRef.current.click();
                                    }
                                }}
                            >
                                {t('dropzone.changeFile')}
                            </Button>
                        </VStack>
                    </>
                ) : (
                    <>
                        <Square size="10" bg="bg.subtle" borderRadius="lg">
                            <Icon as={FiUploadCloud} boxSize="5" color="fg.muted" />
                        </Square>
                        <VStack spacing="1">
                            <HStack spacing="1" whiteSpace="nowrap">
                                <Button
                                    variant="text"
                                    colorScheme="blue"
                                    size="sm"
                                    onClick={() => {
                                        if (fileInputRef.current) {
                                            fileInputRef.current.click();
                                        }
                                    }}
                                >
                                    {t('dropzone.uploadClickCta')}
                                </Button>
                                <Text textStyle="sm" color="fg.muted">
                                    {t('dropzone.ctaAlternative')}
                                </Text>
                            </HStack>
                            <Text textStyle="xs" color="fg.muted">
                                {guidance}
                            </Text>
                        </VStack>
                    </>
                )}
                <input
                    type="file"
                    ref={fileInputRef}
                    style={{ display: 'none' }}
                    onChange={handleFileInputChange}
                />
            </VStack>
        </Center>);
};
