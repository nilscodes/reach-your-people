import { GetServerSideProps } from 'next';

export const getServerSideProps: GetServerSideProps = async () => {
  return {
    redirect: {
      destination: 'https://app.termly.io/document/terms-of-use-for-saas/7a266cd3-f4f6-464e-8e0a-28f7a07ba7e0',
      permanent: false,
    },
  };
};

const Terms = () => {
  return null; // This component will not be rendered
};

export default Terms;