FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV RABBITMQ_PASSWORD=""
ENV RYP_POSTGRES_URL=""
ENV RYP_POSTGRES_USER=""
ENV RYP_POSTGRES_PASSWORD=""
ENV REDIS_PASSWORD=""
ENV RYP_API_KEY=""
ENV CARDANO_RECEIVE_ADDRESS=""

EXPOSE 8075
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Dspring.datasource.url=\"${RYP_POSTGRES_URL}\" -Dspring.datasource.username=${RYP_POSTGRES_USER} -Dspring.datasource.password=${RYP_POSTGRES_PASSWORD} -Dspring.rabbitmq.password=${RABBITMQ_PASSWORD} -Dspring.data.redis.password=${REDIS_PASSWORD} -Dio.vibrantnet.ryp.security.api-key=${RYP_API_KEY} -Dio.vibrantnet.ryp.payment.receiveAddress=${CARDANO_RECEIVE_ADDRESS} ${JAVA_OPTS} /app/app.jar" \
]
