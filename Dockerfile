FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY . .
RUN mvn -B clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/continuous-logger-jar-with-dependencies.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

