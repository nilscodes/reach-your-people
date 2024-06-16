import * as React from "react"
import {
  ImageProps,
  useColorModeValue as mode
} from "@chakra-ui/react"
import logo from "../../public/ryp_text.png"
import logoDark from "../../public/ryp_text_dark.png"
import vibrantLogo from "../../public/vibrant_text.png"
import vibrantLogoDark from "../../public/vibrant_text_dark.png"
import Image from "./Image"

export const Logo = (props: ImageProps) => {
  return <Image src={mode(logo, logoDark)} {...props} alt="RYP Logo" />
}

export const VibrantLogo = (props: ImageProps) => {
  return <Image src={mode(vibrantLogo, vibrantLogoDark)} {...props} alt="Vibrant Logo" />
}