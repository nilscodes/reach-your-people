FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8072
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar ${JAVA_OPTS} /app/app.jar" \
]
