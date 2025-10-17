# ============================
# Stage 1: Build the JAR
# ============================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# copy source và build
COPY . .
RUN mvn -q clean package -DskipTests

# ============================
# Stage 2: Run the app
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
