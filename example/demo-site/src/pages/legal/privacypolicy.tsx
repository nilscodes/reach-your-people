import { GetServerSideProps } from 'next';

export const getServerSideProps: GetServerSideProps = async () => {
  return {
    redirect: {
      destination: 'https://app.termly.io/document/privacy-policy/03f7e652-321e-4bc6-a043-a7880d90b223',
      permanent: false,
    },
  };
};

const PrivacyPolicy = () => {
  return null; // This component will not be rendered
};

export default PrivacyPolicy;