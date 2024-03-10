import { Box, Container, Stack, Text, Button } from '@chakra-ui/react'
import ProjectCard from './ProjectCard'
import ProjectGrid from './ProjectGrid'
import { useApi } from '@/contexts/ApiProvider';
import { Project } from '@/lib/types/Project';
import { useEffect, useState } from 'react';
import { Account } from '../../lib/ryp-subscription-api';
import NextLink from '../NextLink';
import ProjectsHeader from './ProjectsHeader';

type ProjectsHomepageProps = {
    account: Account;
};

export default function ProjectsHomepage(props: ProjectsHomepageProps) {
    const api = useApi();
    const [projects, setProjects] = useState<Project[]>([]);
    const [isProjectsLoading, setIsProjectsLoading] = useState(true);

    useEffect(() => {
        api.getProjectsForAccount().then((projects) => {
            setProjects(projects);
            setIsProjectsLoading(false);
        });
    }, [api]);

    const isFirstProject = projects.length === 0;

    return (<Box
        maxW="7xl"
        mx="auto"
        px={{ base: '4', md: '8', lg: '12' }}
        py={{ base: '6', md: '8', lg: '12' }}
    >
        <ProjectsHeader
            title="Your Projects"
            description="See the projects you have a role in, publish announcements and set up your project details."
        >
            <NextLink href="/projects/new">
                <Button variant={isFirstProject && !isProjectsLoading ? 'solid' : 'outline'}>Add new</Button>
            </NextLink>
        </ProjectsHeader>
        {isFirstProject && !isProjectsLoading && <Container py={{ base: '4', md: '8' }}>
            <Stack spacing="4" direction={{ base: 'row', md: 'column' }}>
                <Text textStyle="lg" fontWeight="medium">You don&apos;t have any projects yet.</Text>
                <Text textStyle="sm" color="fg.muted">Create a new project to get started.</Text>
            </Stack>
        </Container>}
        {!isFirstProject && <ProjectGrid>
            {projects.map((project) => (
                <ProjectCard key={project.id} project={project} />
            ))}
        </ProjectGrid>}
    </Box>);
}