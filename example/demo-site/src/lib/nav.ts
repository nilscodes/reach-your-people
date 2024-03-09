import ProjectCategory from "./types/ProjectCategory";

export type NavItem = {
    label: string;
    path: string;
    onlyLoggedIn?: boolean;
    external?: boolean;
    children?: NavItem[];
};

const subscriptionLinks: NavItem[] = Object.keys(ProjectCategory).map((key) => {
    const category = ProjectCategory[key as keyof typeof ProjectCategory];
    return {
        label: category,
        path: `/subscriptions/${category.toLowerCase()}`,
    };
});

const nav: NavItem[] = [{
    label: 'Verify NFT Project Publisher',
    path: '/test',
  }, {
    label: 'Accounts',
    path: '/dashboard',
    onlyLoggedIn: true,
  }, {
    label: 'Subscriptions',
    path: '/subscriptions',
//    children: subscriptionLinks,
  }, {
    label: 'Projects',
    path: '/projects',
    onlyLoggedIn: true,
  }, {
    label: 'Wallets',
    path: '/wallets',
    onlyLoggedIn: true,
  }
];

export default nav;