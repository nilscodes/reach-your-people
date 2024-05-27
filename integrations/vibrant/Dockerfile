FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV VIBRANT_OAUTH_RYP_SECRET=""

EXPOSE 8070
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Doauth2.clients.ryp.client-secret=${VIBRANT_OAUTH_RYP_SECRET} ${JAVA_OPTS} /app/app.jar" \
]
