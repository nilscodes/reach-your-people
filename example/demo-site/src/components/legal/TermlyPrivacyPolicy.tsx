import { Box } from '@chakra-ui/react';
import { useEffect } from 'react';

const iframeId = '19b8cd65-bd75-44fe-8a15-1f2eb683a8ce';

const TermlyPrivacyPolicy = () => {
  useEffect(() => {
    const scriptId = 'termly-jssdk';

    const existingScript = document.getElementById(scriptId);
    if (existingScript) {
      existingScript.remove();
    }

    if (!document.getElementById(scriptId)) {
      const script = document.createElement('script');
      script.id = scriptId;
      script.src = 'https://app.termly.io/embed-policy.min.js';
      script.async = true;
      document.body.appendChild(script);
    }
    
  }, []);

  return (
    <Box w="5xl" mx="auto" data-id={iframeId} {...({ name: 'termly-embed' } as any)}></Box>
  );
  
};

export default TermlyPrivacyPolicy;
