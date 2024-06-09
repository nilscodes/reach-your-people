FROM eclipse-temurin:21-jdk-alpine
COPY build/libs/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""
ENV RYP_BASE_URL=""

EXPOSE 8074
ENTRYPOINT [ "sh", \
    "-c", \
    "java -jar -D-Dio.vibrantnet.ryp.base-url=${RYP_BASE_URL} ${JAVA_OPTS} /app/app.jar" \
]
