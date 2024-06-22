import { useState, useEffect } from 'react';
import SubscriptionsHeader from './SubscriptionsHeader';
import { useApi } from '../../contexts/ApiProvider';
import SubscriptionsGridView from './SubscriptionsGridView';
import SubscriptionsListView from './SubscriptionsListView';
import StandardContentWithHeader from '../StandardContentWithHeader';
import ProjectViewType from '../../lib/types/ProjectViewType';
import ProjectCategory, { ProjectCategoryNames } from '../../lib/types/ProjectCategory';
import { Project } from '@/lib/types/Project';
import { Subscription } from '@/lib/types/Subscription';
import { SubscriptionsContext } from '@/contexts/SubscriptionsProvider';
import useTranslation from 'next-translate/useTranslation';
import { useRouter } from 'next/router';
import { categories } from './_data'
import { SubscriptionsDashboardProps, VerifiedFilterValue } from './types';

const categoryFromPath = (path: string) => {
  const category = path.split('/').pop();
  if (category === 'all') {
    return [];
  }
  const selected = categories.find((cat) => cat.type === category)?.category || ProjectCategory.DeFi;
  return [selected];
}

const matchesVerifiedStatus = (project: Project, verifiedFilterValue: VerifiedFilterValue) => {
  if (verifiedFilterValue === 'verifiedBoth') {
    return true;
  }
  if (verifiedFilterValue === 'verified') {
    return project.manuallyVerified !== null;
  }
  return project.manuallyVerified === null;
}

export const SubscriptionsDashboard = (props: SubscriptionsDashboardProps) => {
  const { account } = props;
  const api = useApi();
  const [isProjectsLoading, setIsProjectsLoading] = useState(true);
  const [projects, setProjects] = useState<Project[]>([]);
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [isListView, setIsListView] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const router = useRouter();
  const currentCategory = router.query.category as string;
  const [typeFilterValue, setTypeFilterValue] = useState<ProjectCategory[]>(categoryFromPath(router.query.category as string));
  const [verifiedFilterValue, setVerifiedFilterValue] = useState<VerifiedFilterValue>('verifiedBoth');
  const [filteredProjects, setFilteredProjects] = useState<Project[]>([]);
  const { t } = useTranslation('projects');
  const { t: tc } = useTranslation('common');

  const onChangeType = (val: string) => {
    setIsListView(val === 'list');
    window.localStorage.setItem('isListView', val === 'list' ? 'true' : 'false');
  };

  const onSearch = (val: string) => {
    setSearchTerm(val.toLowerCase());
  };

  const onChangeFilter = (value: string[]) => {
    setTypeFilterValue(value.map((item) => item as ProjectCategory));
  };

  useEffect(() => {
    const isListView = window.localStorage.getItem('isListView');
    setIsListView(isListView === 'true');
  }, []);

  useEffect(() => {
    if (router.query.search) {
      setSearchTerm(router.query.search as string);
    }
  }, [router.query]);

  useEffect(() => {
    const filtered = projects.filter((item) => ((item.name.toLowerCase().includes(searchTerm)
    || item.url?.toLowerCase().includes(searchTerm)
    || item.category.toLowerCase().includes(searchTerm)
    || item.tags?.some((tag) => tag.toLowerCase().includes(searchTerm)))
    && (typeFilterValue.length === 0 || typeFilterValue.includes(item.category)))
    && matchesVerifiedStatus(item, verifiedFilterValue));
    setFilteredProjects(filtered);
    if (projects.length > 0) { // Only mark loading finished once we have projects and have filtered them once
      setIsProjectsLoading(false);
    }
  }, [typeFilterValue, searchTerm, projects, verifiedFilterValue]);

  const filterData = {
    defaultValue: typeFilterValue,
    options: [
      { label: tc(`categories.${ProjectCategoryNames[ProjectCategory.DeFi]}.name`), value: ProjectCategory.DeFi, count: projects.filter((item) => item.category === ProjectCategory.DeFi).length },
      { label: tc(`categories.${ProjectCategoryNames[ProjectCategory.NFT]}.name`), value: ProjectCategory.NFT, count: projects.filter((item) => item.category === ProjectCategory.NFT).length },
      { label: tc(`categories.${ProjectCategoryNames[ProjectCategory.SPO]}.name`), value: ProjectCategory.SPO, count: projects.filter((item) => item.category === ProjectCategory.SPO).length },
      { label: tc(`categories.${ProjectCategoryNames[ProjectCategory.dRep]}.name`), value: ProjectCategory.dRep, count: projects.filter((item) => item.category === ProjectCategory.dRep).length },
      { label: tc(`categories.${ProjectCategoryNames[ProjectCategory.DAO]}.name`), value: ProjectCategory.DAO, count: projects.filter((item) => item.category === ProjectCategory.DAO).length },
      { label: tc(`categories.${ProjectCategoryNames[ProjectCategory.Other]}.name`), value: ProjectCategory.Other, count: projects.filter((item) => item.category === ProjectCategory.Other).length },
    ],
  };

  useEffect(() => {
    const fetchData = async () => {
      const projectsPromise = api.getProjects();
      const subscriptionsPromise = account !== null ? api.getSubscriptions() : Promise.resolve([]);

      const [projectsData, subscriptionsData] = await Promise.all([projectsPromise, subscriptionsPromise]);
      setProjects(projectsData);
      setSubscriptions(subscriptionsData);
    };

    fetchData();
  }, [api, account]);

  return (<StandardContentWithHeader
    header={<SubscriptionsHeader
      title={t('allProjects')}
      currentType={isListView ? ProjectViewType.list : ProjectViewType.card}
      onChangeType={onChangeType}
      searchTerm={searchTerm}
      onSearch={onSearch}
      filterData={filterData}
      onChangeFilter={onChangeFilter}
      verifiedFilterValue={verifiedFilterValue}
      onChangeVerifiedFilter={(value) => setVerifiedFilterValue(value)}
    />}
    px="0">
      <SubscriptionsContext.Provider value={{ subscriptions, setSubscriptions }}>
        {isListView && <SubscriptionsListView account={account} projects={filteredProjects} subscriptions={subscriptions} isProjectsLoading={isProjectsLoading} currentCategory={currentCategory} />}
        {!isListView && <SubscriptionsGridView account={account} projects={filteredProjects} subscriptions={subscriptions} isProjectsLoading={isProjectsLoading} currentCategory={currentCategory} />}
      </SubscriptionsContext.Provider>
    </StandardContentWithHeader>);
};
``