import * as React from "react"
import {
  ImageProps,
  useColorModeValue as mode
} from "@chakra-ui/react"
import logo from "../../public/vibrant_text.png"
import logoDark from "../../public/vibrant_text_dark.png"
import Image from "./Image"

export const Logo = (props: ImageProps) => {
  return <Image src={mode(logo, logoDark)} {...props} alt="Vibrant Logo" />
}