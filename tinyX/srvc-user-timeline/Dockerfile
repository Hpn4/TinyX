FROM registry.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/maven_build AS build
WORKDIR /build

COPY . .
RUN mvn install -pl srvc-user-timeline -am -DskipTests
RUN mvn package -pl srvc-user-timeline -am -Dquarkus.package.type=uber-jar -DskipTests

FROM registry.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/maven_prod
WORKDIR /app
COPY --from=build build/srvc-user-timeline/target/*-runner.jar ./service.jar
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
