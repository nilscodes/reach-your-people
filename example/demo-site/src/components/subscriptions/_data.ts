import ProjectCategory from "@/lib/types/ProjectCategory"

export const categories = [
  {
    type: 'defi',
    imageUrl:
      '/defi.jpg',
    category: ProjectCategory.DeFi
  },
  {
    type: 'spo',
    imageUrl:
      '/spo.jpg',
    category: ProjectCategory.SPO
  },
  {
    type: 'drep',
    imageUrl:
      '/drep.jpg',
    category: ProjectCategory.dRep
  },
  {
    type: 'nft',
    imageUrl:
      '/nft.jpg',
    category: ProjectCategory.NFT
  },
  {
    type: 'other',
    imageUrl:
      '/other.jpg',
    category: ProjectCategory.Other
  },
  {
    type: 'dao',
    imageUrl:
      '/dao.jpg',
    category: ProjectCategory.DAO
  },
]

export type ElementType<T extends ReadonlyArray<unknown>> = T extends ReadonlyArray<
  infer ElementType
>
  ? ElementType
  : never

export type Category = ElementType<typeof categories>