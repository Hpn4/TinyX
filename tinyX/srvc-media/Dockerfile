FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

COPY . .
RUN mvn install -pl srvc-media -am -DskipTests
RUN mvn package -pl srvc-media -am -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build build/srvc-media/target/*-runner.jar ./service.jar
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
