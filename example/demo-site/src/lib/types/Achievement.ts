export type Achievement = {
  id: string
  title: string
  description: string
  type: 'numeric' | 'boolean' | 'text'
  achieved?: boolean
  points?: number
  hasMax?: boolean
  maxPoints?: number
}