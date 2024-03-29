import {
    Box,
    BoxProps,
    Stack,
    StackDivider,
    Switch,
    Tag,
    Text,
    Wrap,
  } from '@chakra-ui/react'
import NextLink from '../NextLink';
import useTranslation from 'next-translate/useTranslation';
import Trans from 'next-translate/Trans';
  
  export default function WalletSettings(props: BoxProps) {
    const { t } = useTranslation('wallets');
    return (<Box as="form" bg="bg.surface" boxShadow="sm" borderRadius="lg" {...props}>
      <Stack spacing="5" px={{ base: '4', md: '6' }} py={{ base: '5', md: '6' }} divider={<StackDivider />}>
        <Stack justify="space-between" direction="row" spacing="16">
          <Stack spacing="0.5" fontSize="sm">
            <Text color="fg.emphasized" fontWeight="bold">
              {t('settings.tokens.title')}
            </Text>
            <Text color="fg.muted">
              <Trans i18nKey='wallets:settings.tokens.description' components={[<NextLink key="" href='/subscriptions' />]}></Trans>
            </Text>
          </Stack>
          <Switch defaultChecked={true} />
        </Stack>
        <Stack justify="space-between" direction="row" spacing="16">
          <Stack spacing="0.5" fontSize="sm">
            <Text color="fg.emphasized" fontWeight="bold">
              {t('settings.spo.title')}
            </Text>
            <Text color="fg.muted">
              <Trans i18nKey='wallets:settings.spo.description' components={[<NextLink key="" href='/subscriptions' />]}></Trans>
            </Text>
            <Wrap spacing="2"><Tag>HAZEL</Tag></Wrap>
          </Stack>
          <Switch defaultChecked={true} />
        </Stack>
        <Stack justify="space-between" direction="row" spacing="16">
          <Stack spacing="0.5" fontSize="sm">
            <Text color="fg.emphasized" fontWeight="bold">
              {t('settings.drep.title')}
            </Text>
            <Text color="fg.muted">
              <Trans i18nKey='wallets:settings.drep.description' components={[<NextLink key="" href='/subscriptions' />]}></Trans>
            </Text>
          </Stack>
          <Switch defaultChecked={true} />
        </Stack>
      </Stack>
    </Box>);
  }
  