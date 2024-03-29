enum ProjectCategory {
  DeFi = 'DeFi',
  NFT = 'NFT',
  SPO = 'SPO',
  dRep = 'dRep',
  DAO = 'DAO',
  Other = 'Other',
}

export const ProjectCategoryNames: Record<ProjectCategory, string> = {
  [ProjectCategory.DeFi]: 'defi',
  [ProjectCategory.NFT]: 'nft',
  [ProjectCategory.SPO]: 'spo',
  [ProjectCategory.dRep]: 'drep',
  [ProjectCategory.DAO]: 'dao',
  [ProjectCategory.Other]: 'other',
};

export default ProjectCategory;
