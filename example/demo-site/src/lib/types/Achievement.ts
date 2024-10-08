import { iconMap } from "../achievements";

type IconMap = typeof iconMap;
type IconKey = keyof IconMap;

export type Achievement = {
  id: string
  title: string
  description: string
  type: 'numeric' | 'boolean' | 'text'
  achieved?: boolean
  points?: number
  hasMax?: boolean
  maxPoints?: number
  icon: IconKey
}