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
    label: 'nav.verify',
    path: '/test',
  }, {
    label: 'nav.accounts',
    path: '/dashboard',
    onlyLoggedIn: true,
  }, {
    label: 'nav.subscriptions',
    path: '/subscriptions',
//    children: subscriptionLinks,
  }, {
    label: 'nav.projects',
    path: '/projects',
    onlyLoggedIn: true,
  }, {
    label: 'nav.wallets',
    path: '/wallets',
    onlyLoggedIn: true,
  }
];

export default nav;