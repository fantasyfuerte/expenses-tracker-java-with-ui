# Build stage
FROM eclipse-temurin:25-jdk AS build
WORKDIR /build
COPY *.java ./
RUN mkdir -p out && javac -d out *.java

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/out ./out

# Swing app needs an X11 display. Pass it at runtime:
#   docker run --rm -e DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix \
#     -v "$HOME/.Xauthority:/home/app/.Xauthority:ro" expenses-tracker
RUN useradd --create-home --uid 1000 app \
    && chown -R app:app /app
USER app

ENTRYPOINT ["java", "-cp", "/app/out", "App"]
