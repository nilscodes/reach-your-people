import React, { useEffect } from 'react';
import { 
  Button,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  useDisclosure,
  Image,
  Text,
  Box,
  VStack,
} from '@chakra-ui/react';
import useTranslation from 'next-translate/useTranslation';
import Trans from 'next-translate/Trans';

export const VibrantSyncStatusMessage = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { t } = useTranslation('accounts');

  useEffect(() => {
    onOpen();
  }, [onOpen]);

  return (
    <>
      <Modal isOpen={isOpen} onClose={onClose} isCentered size={{ base: 'sm', lg: 'xl' }}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{t('vibrantSyncSuccessHeader')}</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack>
              <Image 
                src="https://www.vibrantnet.io/logo512.png" // Replace with the URL of the success image
                alt={t('vibrantSyncSuccessImageAltText')}
                w='200px'
                mb={4}
                mx="auto"
              />
              <Text fontSize="lg">
                <Trans i18nKey='accounts:vibrantSyncSuccessContent' components={[<strong key="" />, <em key="" />]}></Trans>
              </Text>
              <Text fontSize="lg">
                {t('vibrantSyncSuccessContentThanks')}
              </Text>
            </VStack>
          </ModalBody>
          <ModalFooter>
            <Button onClick={onClose}>
            {t('vibrantSyncSuccessContinueButton')}
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
}
