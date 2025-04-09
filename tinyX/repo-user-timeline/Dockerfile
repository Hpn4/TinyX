FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

COPY . .
RUN mvn install -pl repo-user-timeline -am -DskipTests
RUN mvn package -pl repo-user-timeline -am -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build build/repo-user-timeline/target/*-runner.jar ./service.jar
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
