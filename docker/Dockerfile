FROM amazoncorretto:17

WORKDIR /app

# 빌드된 bootJar를 컨테이너로 복사
COPY orderping-api/build/libs/orderping-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]