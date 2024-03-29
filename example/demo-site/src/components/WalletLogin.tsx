import { Button, Stack, Img } from '@chakra-ui/react'
import { Wallet } from '@meshsdk/core'
import useTranslation from 'next-translate/useTranslation';
import { FaLeftLong } from 'react-icons/fa6';

type WalletLoginProps = {
  wallets: Wallet[];
  onReturn: () => void;
  handleSignIn: (selectedWallet: string) => void;
}

export default function WalletLogin({
  wallets, onReturn, handleSignIn
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
</Stack>);
}