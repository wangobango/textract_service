FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /build
COPY src /build/src
COPY pom.xml /build
RUN mvn -f /build/pom.xml clean install
ARG JAR_FILE=/build/target/*.jar

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /build/target/textract-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]