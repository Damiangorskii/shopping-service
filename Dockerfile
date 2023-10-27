FROM openjdk:17-jdk

WORKDIR /app

LABEL maintainer="damian" \
      version="1.0" \
      description="Docker image for the shopping-service"

COPY target/shopping-service-0.0.1-SNAPSHOT.jar /app/shopping-service.jar

EXPOSE 8083

CMD ["java", "-jar", "/app/shopping-service.jar"]