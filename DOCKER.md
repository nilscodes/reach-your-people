# Containerization guide

## Introduction

This short guide will help you deploy the core services of the Reach Your People platform using Docker Compose.

## Requirements

- Ubuntu 24.04
- A sudo-enabled user (we assume for now it is named `ryp`)
- A Blockfrost API key for Cardano mainnet (only required for testing the core verification service)

## Installation of Docker Compose

- Log in as the ryp user and change into their home directory
- Run the following commands, in order
  - `sudo apt install apt-transport-https ca-certificates curl software-properties-common`
  - `curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -`
  - `sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu noble stable"`
  - `sudo apt-get install docker.io docker-compose-v2`
  - `sudo systemctl enable docker`
  - `sudo systemctl start docker`
  - `sudo usermod -aG docker ryp`
  - `sudo reboot`
- Wait for the machine to come back up

## Deployment of the services

- Log in again as the ryp user and run the below command, which will get the compose files and the Redis configuration file:
  - `mkdir ryp && cd ryp && wget https://raw.githubusercontent.com/nilscodes/reach-your-people/main/docker-compose.prod.yml && wget https://raw.githubusercontent.com/nilscodes/reach-your-people/main/docker-compose.yml && mkdir -p example/docker && cd example/docker && wget https://raw.githubusercontent.com/nilscodes/reach-your-people/main/example/docker/redis.conf && cd ../..`
- Edit `docker-compose.yml` and set the following variables:
  - `INDEXER_TYPE=blockfrost`
  - Remove all services that start with `integration`. They require signing up for the respective services and are out of the scope of this core service deployment documentation. If you were to deploy them, the process is the same, simply fill out the required environment variables.
  - Save the file
- Edit `docker-compose.prod.yml` and set the following variables:
  - `RABBITMQ_PASSWORD=there_is_a_good_rabbit`
  - `POSTGRES_PASSWORD=escape_the_sequel`
  - `MONGODB_PASSWORD=to_the_mongo_and_back`
  - `REDIS_PASSWORD=your_strong_password_here`
  - Set `BLOCKFROST_PROJECT_ID=` to your Blockfrost project key.
  - Remove all services that start with "integration" (see above)
- Now run `docker compose -f ./docker-compose.yml -f ./docker-compose.prod.yml up -d` to start the services

## Initialization of the PostgreSQL database

Everything generally runs out of the box, but you will need to initialize the `ryp` SQL database running in the `ryp_postgres_prod` container with the following scripts:

- [Core subscription service SQL scripts](./core-subscription/src/main/resources/schema-postgres.sql)
- [Core points service SQL scripts](./core-points/src/main/resources/schema-postgres.sql)
- [Core billing service SQL scripts](./core-billing/src/main/resources/schema-postgres.sql)

Once the SQL scripts have been deployed, the services are ready to use.

## Testing

You can test the individual services with the following instructions

### Core verification service

Run this command

`curl -X GET "http://localhost:8070/cip66/df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058/discord/876912038495014933" -H "accept: application/json"`

It may take a while due to IPFS gateways not immediately responding, but will return true for a Cardano mainnet test policy that has been verified with a given Discord account. Can also be tested with any other valid policy ID, where it would immediately return false.

`curl -X GET "http://localhost:8071/actuator/health" -H "accept: application/json"`

Will show the health check for the verification service

### Core subscription service

Run this command

`curl -X GET "http://localhost:8071/projects" -H "accept: application/json"`

It will show an empty list of projects (indicating database connectivity)

`curl -X GET "http://localhost:8071/actuator/health" -H "accept: application/json"`

Will show the health check for the subscription service

### Core publishing service

Run this command

`curl -X GET "http://localhost:8072/projects/1/announcements" -H "accept: application/json"`

It will show an empty list of announcements

`curl -X GET "http://localhost:8072/actuator/health" -H "accept: application/json"`

Will show the health check for the publishing service

### Core redirect service

Run this command

`curl -X GET "http://localhost:8074/urls/projects/1" -H "accept: application/json"`

It will show an empty list of URLs for a project, indicating database availability

`curl -X GET "http://localhost:8074/actuator/health" -H "accept: application/json"`

Will show the health check for the redirect service

### Core points service

Run this command

`curl -X GET "http://localhost:8075/tokens" -H "accept: application/json"`

It will show an empty list of claim token options, indicating database availability

`curl -X GET "http://localhost:8075/actuator/health" -H "accept: application/json"`

Will show the health check for the points service

### Core billing service

Run this command

`curl -X GET "http://localhost:8076/billing/accounts/1" -H "accept: application/json"`

It will show an empty list of bills for a given account, indicating database availability

`curl -X GET "http://localhost:8076/actuator/health" -H "accept: application/json"`

Will show the health check for the billing service


