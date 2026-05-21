FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml settings.gradle build.gradle ./
COPY src ./src

RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/target/df-delta-engine-v2-*.jar /app/app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
