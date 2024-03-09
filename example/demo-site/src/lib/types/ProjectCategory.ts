enum ProjectCategory {
  DeFi = 'DeFi',
  NFT = 'NFT',
  SPO = 'SPO',
  dRep = 'dRep',
  DAO = 'DAO',
  Other = 'Other',
}

export const ProjectCategoryNames: Record<ProjectCategory, string> = {
  [ProjectCategory.DeFi]: 'DeFi',
  [ProjectCategory.NFT]: 'NFT',
  [ProjectCategory.SPO]: 'SPO',
  [ProjectCategory.dRep]: 'dRep',
  [ProjectCategory.DAO]: 'DAO',
  [ProjectCategory.Other]: 'Other',
};

export default ProjectCategory;
