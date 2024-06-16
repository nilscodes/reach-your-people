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
import { Account } from '../../lib/ryp-subscription-api';
import { SubscriptionsContext } from '@/contexts/SubscriptionsProvider';
import useTranslation from 'next-translate/useTranslation';
import { useRouter } from 'next/router';
import { categories } from './_data'

type SubscriptionsDashboardProps = {
  account: Account | null;
  title: string;
  all: boolean;
};

const categoryFromPath = (path: string) => {
  const category = path.split('/').pop();
  if (category === 'all') {
    return [];
  }
  const selected = categories.find((cat) => cat.type === category)?.category || ProjectCategory.DeFi;
  return [selected];
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
  const [typeFilterValue, setTypeFilterValue] = useState<ProjectCategory[]>(categoryFromPath(router.query.category as string));
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
    && (typeFilterValue.length === 0 || typeFilterValue.includes(item.category))));
    setFilteredProjects(filtered);
  }, [typeFilterValue, searchTerm, projects]);

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
      setIsProjectsLoading(false);
    };

    fetchData();
  }, [api, account]);

  return (<StandardContentWithHeader
    header={<SubscriptionsHeader title={t('allProjects')} currentType={isListView ? ProjectViewType.list : ProjectViewType.card} onChangeType={onChangeType} searchTerm={searchTerm} onSearch={onSearch} filterData={filterData} onChangeFilter={onChangeFilter} />}
    px="0">
      <SubscriptionsContext.Provider value={{ subscriptions, setSubscriptions }}>
        {isListView && <SubscriptionsListView account={account} projects={filteredProjects} subscriptions={subscriptions} isProjectsLoading={isProjectsLoading} />}
        {!isListView && <SubscriptionsGridView account={account} projects={filteredProjects} subscriptions={subscriptions} isProjectsLoading={isProjectsLoading} />}
      </SubscriptionsContext.Provider>
    </StandardContentWithHeader>);
};
``