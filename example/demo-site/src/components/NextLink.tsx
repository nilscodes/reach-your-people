import { Link, LinkProps } from '@chakra-ui/next-js'

export default function NextLink(props: LinkProps) {
  return <Link {...props} _hover={{textDecoration:'none'}} />
}
