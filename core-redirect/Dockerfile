FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV RYP_BASE_URL=""
ENV MONGODB_PASSWORD=""
ENV REDIS_PASSWORD=""
ENV RYP_API_KEY=""

EXPOSE 8074
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Dio.vibrantnet.ryp.base-url=${RYP_BASE_URL} -Dspring.data.mongodb.password=${MONGODB_PASSWORD} -Dspring.data.redis.password=${REDIS_PASSWORD} -Dio.vibrantnet.ryp.security.api-key=${RYP_API_KEY} ${JAVA_OPTS} /app/app.jar" \
]
