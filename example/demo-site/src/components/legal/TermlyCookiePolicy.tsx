import { Box } from '@chakra-ui/react';
import { useEffect } from 'react';

const iframeId = 'b6210b5c-75d9-4877-85cf-f43dd5f1aef8';

const TermlyCookiePolicy = () => {
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

export default TermlyCookiePolicy;
