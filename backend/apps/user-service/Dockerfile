FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
COPY backend/.mvn ./.mvn
COPY backend/mvnw ./mvnw
COPY backend/utils ./utils
COPY backend/apps/user-service ./apps/user-service
RUN chmod +x mvnw && ./mvnw -pl apps/user-service -am -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/apps/user-service/target/*.jar /app/app.jar
EXPOSE 5001
ENTRYPOINT ["java","-jar","/app/app.jar"]
