FROM maven:3.6.0-jdk-11-slim AS build
EXPOSE 8080
USER root

WORKDIR /app
COPY ./ /app
RUN chmod +x /app

RUN mvn -f /app/pom.xml clean package

FROM openjdk:11-jre-slim
COPY --from=build /app/target/otic-0.0.1-SNAPSHOT.jar /app/otic-0.0.1-SNAPSHOT.jar
COPY ./src/main/resources/openapi.yml /openapi.yml

CMD ["java","-jar","/app/otic-0.0.1-SNAPSHOT.jar"]
