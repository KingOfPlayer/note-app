FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
COPY backend/.mvn ./.mvn
COPY backend/mvnw ./mvnw
COPY backend/utils ./utils
COPY backend/apps/note-service ./apps/note-service

COPY backend/apps/file-service/pom.xml ./apps/file-service/pom.xml
COPY backend/apps/user-service/pom.xml ./apps/user-service/pom.xml
COPY backend/apps/gateway-service/pom.xml ./apps/gateway-service/pom.xml

RUN chmod +x mvnw && ./mvnw -pl apps/note-service -am -DskipTests clean package


FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/apps/note-service/target/*.jar /app/app.jar
EXPOSE 5002
ENTRYPOINT ["java","-jar","/app/app.jar"]
