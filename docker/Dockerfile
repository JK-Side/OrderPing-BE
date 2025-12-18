FROM amazoncorretto:17

WORKDIR /app

# 빌드된 bootJar를 컨테이너로 복사
COPY orderping-api/build/libs/orderping-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]