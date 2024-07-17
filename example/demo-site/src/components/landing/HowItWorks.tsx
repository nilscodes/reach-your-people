import { Badge, Box, Button, Container, Heading, IconButton, Img, Stack, Text, useColorModeValue as mode } from '@chakra-ui/react'
import useTranslation from "next-translate/useTranslation";
import { useRouter } from 'next/router';
import { List } from '../ui/List';
import { ListItem } from '../ui/ListItem';
import { MdCheck } from 'react-icons/md';

export default function HowItWorks() {
  const { t } = useTranslation('common')
  const router = useRouter();
  
    return (<Box as="section">
      <Container py={{ base: '16', md: '24' }}>
        <Stack spacing={{ base: '12', md: '16' }}>
          <Stack spacing={{ base: '4', md: '5' }} maxW="4xl">
            <Stack spacing="3">
              <Heading size={{ base: 'sm', md: 'md' }}>
                {t('howitworks.headline')}
              </Heading>
            </Stack>
            <List spacing="2">
              <ListItem title={t('howitworks.step1')} icon={<Heading fontSize="2xl">1</Heading>}>
                <Box p={4}>
                  <Stack>
                    <Text fontSize="lg" fontWeight="semibold">
                      {t('howitworks.step1')}
                    </Text>
                  </Stack>
                </Box>
              </ListItem>
              <ListItem title={t('howitworks.step2')} icon={<Heading fontSize="2xl">2</Heading>}>
                <Box p={4}>
                  <Stack>
                    <Text fontSize="lg" fontWeight="semibold">
                      {t('howitworks.step2')}
                    </Text>
                  </Stack>
                </Box>
              </ListItem>
              <ListItem title={t('howitworks.step3')} icon={<Heading fontSize="2xl">3</Heading>}>
                <Box p={4}>
                  <Stack>
                    <Text fontSize="lg" fontWeight="semibold">
                      {t('howitworks.step3')}
                    </Text>
                  </Stack>
                </Box>
              </ListItem>
              <ListItem title={t('howitworks.done')} icon={<MdCheck size="1.8em" />}>
                <Box p={4}>
                  <Stack>
                    <Text fontSize="lg" fontWeight="semibold">
                      {t('howitworks.done')}
                    </Text>
                  </Stack>
                </Box>
              </ListItem>
            </List>
          </Stack>
        </Stack>
      </Container>
    </Box>);
}
