FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
COPY backend/.mvn ./.mvn
COPY backend/mvnw ./mvnw
COPY backend/utils ./utils
COPY backend/apps/file-service ./apps/file-service

COPY backend/apps/gateway-service/pom.xml ./apps/gateway-service/pom.xml
COPY backend/apps/user-service/pom.xml ./apps/user-service/pom.xml
COPY backend/apps/note-service/pom.xml ./apps/note-service/pom.xml

RUN chmod +x mvnw && ./mvnw -pl apps/file-service -am -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/apps/file-service/target/*.jar /app/app.jar
EXPOSE 5003
ENTRYPOINT ["java","-jar","/app/app.jar"]
