FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV RABBITMQ_PASSWORD=""
ENV MONGODB_PASSWORD=""
ENV REDIS_PASSWORD=""
ENV RYP_API_KEY=""

EXPOSE 8072
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Dspring.rabbitmq.password=${RABBITMQ_PASSWORD} -Dspring.data.mongodb.password=${MONGODB_PASSWORD} -Dspring.data.redis.password=${REDIS_PASSWORD} -Dio.vibrantnet.ryp.security.api-key=${RYP_API_KEY} ${JAVA_OPTS} /app/app.jar" \
]
