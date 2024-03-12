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

type SubscriptionsDashboardProps = {
  account: Account | null;
  title: string;
  all: boolean;
};

export const SubscriptionsDashboard = (props: SubscriptionsDashboardProps) => {
  const { account } = props;
  const api = useApi();
  const [isProjectsLoading, setIsProjectsLoading] = useState(true);
  const [projects, setProjects] = useState<Project[]>([]);
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [isListView, setIsListView] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilterValue, setTypeFilterValue] = useState<ProjectCategory[]>([]);
  const [filteredProjects, setFilteredProjects] = useState<Project[]>([]);

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
    const filtered = projects.filter((item) => (item.name.toLowerCase().includes(searchTerm)
    || item.url?.toLowerCase().includes(searchTerm)
    || item.category.toLowerCase().includes(searchTerm)
    || item.tags?.some((tag) => tag.toLowerCase().includes(searchTerm))
    && (typeFilterValue.length === 0 || typeFilterValue.includes(item.category))));
    setFilteredProjects(filtered);
  }, [typeFilterValue, searchTerm, projects]);

  const filterData = {
    defaultValue: filteredProjects.map((item) => item.category),
    options: [
      { label: ProjectCategoryNames[ProjectCategory.DeFi], value: ProjectCategory.DeFi, count: filteredProjects.filter((item) => item.category === ProjectCategory.DeFi).length },
      { label: ProjectCategoryNames[ProjectCategory.NFT], value: ProjectCategory.NFT, count: filteredProjects.filter((item) => item.category === ProjectCategory.NFT).length },
      { label: ProjectCategoryNames[ProjectCategory.SPO], value: ProjectCategory.SPO, count: filteredProjects.filter((item) => item.category === ProjectCategory.SPO).length },
      { label: ProjectCategoryNames[ProjectCategory.dRep], value: ProjectCategory.dRep, count: filteredProjects.filter((item) => item.category === ProjectCategory.dRep).length },
      { label: ProjectCategoryNames[ProjectCategory.DAO], value: ProjectCategory.DAO, count: filteredProjects.filter((item) => item.category === ProjectCategory.DAO).length },
      { label: ProjectCategoryNames[ProjectCategory.Other], value: ProjectCategory.Other, count: filteredProjects.filter((item) => item.category === ProjectCategory.Other).length },
    ],
  };

  useEffect(() => {
    const fetchData = async () => {
      const projectsPromise = api.getProjects();
      const subscriptionsPromise = api.getSubscriptions();

      const [projectsData, subscriptionsData] = await Promise.all([projectsPromise, subscriptionsPromise]);
      setProjects(projectsData);
      setSubscriptions(subscriptionsData);
      setIsProjectsLoading(false);
    };

    fetchData();
  }, [api]);

  return (<StandardContentWithHeader
    header={<SubscriptionsHeader title={'All Projects'} currentType={isListView ? ProjectViewType.list : ProjectViewType.card} onChangeType={onChangeType} onSearch={onSearch} filterData={filterData} onChangeFilter={onChangeFilter} />}
    px="0">
      <SubscriptionsContext.Provider value={{ subscriptions, setSubscriptions }}>
        {isListView && <SubscriptionsListView account={account} projects={filteredProjects} subscriptions={subscriptions} isProjectsLoading={isProjectsLoading} />}
        {!isListView && <SubscriptionsGridView account={account} projects={filteredProjects} subscriptions={subscriptions} isProjectsLoading={isProjectsLoading} />}
      </SubscriptionsContext.Provider>
    </StandardContentWithHeader>);
};
``