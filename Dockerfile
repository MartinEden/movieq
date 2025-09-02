FROM amazoncorretto:21

WORKDIR /code
COPY gradle gradle
COPY gradlew settings.gradle.kts ./
RUN ./gradlew wrapper

COPY build.gradle.kts ./
RUN ./gradlew buildDependents

EXPOSE 8080
COPY src src/
RUN ./gradlew installDist

ENTRYPOINT ["./build/install/movieq/bin/movieq"]
