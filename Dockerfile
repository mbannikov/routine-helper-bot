FROM gradle:6.5.0-jdk11 AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY ./ /workspace
RUN ./gradlew --no-daemon build -x test

FROM openjdk:11-jre-slim
RUN mkdir /app
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]