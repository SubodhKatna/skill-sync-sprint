FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY discovery-server/pom.xml discovery-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/

# Download dependencies first
RUN mvn dependency:go-offline -B -T 1C

# Copy the rest of the source code
COPY . .
RUN mvn clean package -DskipTests -T 1C

FROM eclipse-temurin:17-jre-alpine
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
