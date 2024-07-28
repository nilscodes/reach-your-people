import { Button, Stack, Img, Heading } from '@chakra-ui/react'
import { Wallet } from '@meshsdk/core'
import useTranslation from 'next-translate/useTranslation';
import { FaLeftLong } from 'react-icons/fa6';

type WalletLoginProps = {
  wallets: Wallet[];
  onReturn: () => void;
  handleSignIn: (selectedWallet: string) => void;
  handleHwSignIn: (selectedWallet: string) => void;
}

export default function WalletLogin({
  wallets, onReturn, handleSignIn, handleHwSignIn
}: WalletLoginProps) {
  const { t } = useTranslation('accounts');
  return(<Stack spacing="3">
  <Button key="back"
      variant="secondary"
      leftIcon={<FaLeftLong />}
      cursor="pointer"
      onClick={onReturn}
    >
      {t('backToSocial')}
    </Button>
  {wallets.map((wallet) => (
    <Button key={wallet.name}
      variant="secondary"
      cursor="pointer"
      leftIcon={<Img src={wallet.icon} alt={wallet.name} h='1.5em' w='1.5em' />}
      onClick={() => {
        handleSignIn(wallet.name);
      }}
    >
      {wallet.name}
    </Button>)
  )}
  <Heading size="xs" mt={6}>{t('hardwareWallet.title')}</Heading>
  {wallets.map((wallet) => (
    <Button key={wallet.name}
      variant="secondary"
      cursor="pointer"
      leftIcon={<Img src={wallet.icon} alt={wallet.name} h='1.5em' w='1.5em' />}
      onClick={() => {
        handleHwSignIn(wallet.name);
      }}
    >
      {t('hardwareWalletName', { walletName: wallet.name })}
    </Button>)
  )}
</Stack>);
}