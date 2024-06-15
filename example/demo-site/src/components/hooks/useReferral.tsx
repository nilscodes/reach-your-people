import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';

type ReferralHook = [number | null, () => void];

const useReferral = (): ReferralHook => {
  const router = useRouter();
  const [referralId, setReferralId] = useState<number | null>(null);

  const completeReferral = () => {
    localStorage.removeItem('referral');
    setReferralId(null);
  }

  useEffect(() => {
    const { ref, ...restQuery } = router.query;

    if (ref) {
      // Store the referral in localStorage
      localStorage.setItem('referral', ref as string);
      setReferralId(+ref);

      // Remove the referral query parameter and update the URL
      const newQuery = new URLSearchParams(restQuery as any).toString();
      const newPath = router.pathname + (newQuery ? `?${newQuery}` : '');

      router.replace(newPath, undefined, { shallow: true });
    } else {
      // Retrieve the referral from localStorage if available
      const storedRef = localStorage.getItem('referral');
      if (storedRef) {
        setReferralId(+storedRef);
      }
    }
  }, [router.query, router]);

  return [referralId, completeReferral];
};

export default useReferral;
