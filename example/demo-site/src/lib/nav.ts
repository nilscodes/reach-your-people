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
        path: `/projects/${category.toLowerCase()}`,
    };
});

const nav: NavItem[] = [{
  //   label: 'nav.verify',
  //   path: '/test',
  // }, {
    label: 'nav.account',
    path: '/account',
    onlyLoggedIn: true,
  }, {
    label: 'nav.projects',
    path: '/projects',
//    children: subscriptionLinks,
  }, {
    label: 'nav.publish',
    path: '/publish',
    onlyLoggedIn: true,
  }
];

export default nav;