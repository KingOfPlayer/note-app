FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
COPY backend/.mvn ./.mvn
COPY backend/mvnw ./mvnw
COPY backend/utils ./utils
COPY backend/apps/user-service ./apps/user-service

COPY backend/apps/file-service/pom.xml ./apps/file-service/pom.xml
COPY backend/apps/gateway-service/pom.xml ./apps/gateway-service/pom.xml
COPY backend/apps/note-service/pom.xml ./apps/note-service/pom.xml

RUN chmod +x mvnw && ./mvnw -pl apps/user-service -am -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/apps/user-service/target/*.jar /app/app.jar
EXPOSE 5001
ENTRYPOINT ["java","-jar","/app/app.jar"]
