import { SessionProvider } from "next-auth/react"
import { AppProps } from 'next/app'
import { ChakraProvider, extendTheme, theme as baseTheme, Box, useColorModeValue as mode, } from '@chakra-ui/react'
import { theme as proTheme } from '@chakra-ui/pro-theme'
import type { Session } from "next-auth"
import "@fontsource/aldrich"
import "@fontsource/rajdhani/500.css"
import Header from '@/components/Header'
import ApiProvider from "@/contexts/ApiProvider"
import { Footer } from "@/components/footer/Footer"


export default function App({ Component, pageProps: { session, ...pageProps } }: AppProps<{ session: Session }>) {
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

  return <SessionProvider session={session}>
    <ChakraProvider theme={theme}>
      <ApiProvider>
        <Box as="section" minH="lg">
          <Header />
          <Box pb={6}>
            <Component {...pageProps} />
          </Box>
          <Footer />
        </Box>
      </ApiProvider>
    </ChakraProvider>
  </SessionProvider>
}