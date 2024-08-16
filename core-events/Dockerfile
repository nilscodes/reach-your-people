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
ENV RYP_API_KEY=""

EXPOSE 8073
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Dspring.datasource.url=\"${DB_SYNC_POSTGRES_URL}\" -Dspring.datasource.username=${DB_SYNC_POSTGRES_USER} -Dspring.datasource.password=${DB_SYNC_POSTGRES_PASSWORD} -Dio.vibrantnet.ryp.type=${INDEXER_TYPE} -Dio.vibrantnet.ryp.blockfrost.url=${BLOCKFROST_URL} -Dio.vibrantnet.ryp.blockfrost.apiKey=${BLOCKFROST_PROJECT_ID} -Dspring.rabbitmq.password=${RABBITMQ_PASSWORD} -Dio.vibrantnet.ryp.security.api-key=${RYP_API_KEY} ${JAVA_OPTS} /app/app.jar" \
]
