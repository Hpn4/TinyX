FROM registry.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/maven_build AS build
WORKDIR /build

COPY . .
RUN mvn install -pl repo-user-timeline -am -DskipTests
RUN mvn package -pl repo-user-timeline -am -Dquarkus.package.type=uber-jar -DskipTests

FROM registry.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/maven_prod
WORKDIR /app
COPY --from=build build/repo-user-timeline/target/*-runner.jar ./service.jar
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
