FROM ubuntu:22.04 AS libsodium

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y automake build-essential pkg-config libffi-dev libgmp-dev libssl-dev libtinfo-dev libsystemd-dev \
    zlib1g-dev make g++ tmux git jq curl libncursesw5 libtool autoconf llvm libnuma-dev \
    && apt-get clean

# Install Libsodium
WORKDIR /build/libsodium
RUN git clone https://github.com/IntersectMBO/libsodium && \
    cd libsodium && \
    git checkout dbb48cce5429cb6585c9034f002568964f1ce567 && \
    ./autogen.sh && ./configure && make && make install

FROM eclipse-temurin:21-jdk-jammy
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV DB_SYNC_POSTGRES_URL=""
ENV DB_SYNC_POSTGRES_USER=""
ENV DB_SYNC_POSTGRES_PASSWORD=""
ENV INDEXER_TYPE="cardano-db-sync"
ENV BLOCKFROST_URL=""
ENV BLOCKFROST_PROJECT_ID=""
ENV RABBITMQ_PASSWORD=""
ENV MONGODB_PASSWORD=""
ENV REDIS_PASSWORD=""

ENV LD_LIBRARY_PATH="/usr/local/lib"
ENV PKG_CONFIG_PATH="/usr/local/lib/pkgconfig"

COPY --from=libsodium /usr/local/lib /usr/local/lib

EXPOSE 8070
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Dspring.datasource.url=\"${DB_SYNC_POSTGRES_URL}\" -Dspring.datasource.username=${DB_SYNC_POSTGRES_USER} -Dspring.datasource.password=${DB_SYNC_POSTGRES_PASSWORD} -Dio.vibrantnet.ryp.type=${INDEXER_TYPE} -Dio.vibrantnet.ryp.blockfrost.url=${BLOCKFROST_URL} -Dio.vibrantnet.ryp.blockfrost.apiKey=${BLOCKFROST_PROJECT_ID} -Dspring.rabbitmq.password=${RABBITMQ_PASSWORD} -Dspring.data.mongodb.password=${MONGODB_PASSWORD} -Dspring.data.redis.password=${REDIS_PASSWORD} ${JAVA_OPTS} /app/app.jar" \
]
