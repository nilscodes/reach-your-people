FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV RABBITMQ_PASSWORD=""

EXPOSE 8072
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -Dspring.rabbitmq.password=${RABBITMQ_PASSWORD} ${JAVA_OPTS} /app/app.jar" \
]
