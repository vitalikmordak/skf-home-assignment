FROM openjdk:8-jdk as BUILD_IMAGE
COPY . .
RUN ./gradlew clean build

FROM openjdk:8-jre-slim
COPY --from=BUILD_IMAGE /build/libs/home-assignment-release.jar .
EXPOSE 8080
CMD ["java", "-jar", "home-assignment-release.jar"]
