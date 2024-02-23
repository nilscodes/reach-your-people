'use client'

import { ChakraProvider, extendTheme, theme as baseTheme } from '@chakra-ui/react'
import { theme as proTheme } from '@chakra-ui/pro-theme'

export async function ClientProviders({ children }: { children: React.ReactNode }) {
  const theme = extendTheme(proTheme, {
    config: {
      initialColorMode: 'system',
    },
    colors: {
      orange: {
        200: baseTheme.colors.orange['400'],
      },
      brand: {"50":"#FFC42D","100":"#ffc42d","200":"#ffaa34","300":"#ff913b","400":"#ff7842","500":"#ff5f49","600":"#ff4650","700":"#ff2d57","800":"#ff145f","900":"#FF145F"}
    },
    fonts: {
      heading: 'Aldrich',
      body: 'Rajdhani',
    },
    styles: {
      global: {
        "*::placeholder": {
          opacity: 1,
          color: "chakra-placeholder-color"
        },
        "*, *::before, &::after": {
          borderColor: "chakra-border-color",
        },
      }
    }
  });

  return <ChakraProvider theme={theme}>
      {children}
  </ChakraProvider>
}