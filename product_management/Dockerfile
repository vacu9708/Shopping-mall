FROM openjdk:17-jdk-slim-buster

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/app.jar /app/app.jar

# AWS credentials
COPY credentials credentials

# Expose the ports that the Spring Boot application will run on
EXPOSE 8083

# Set the entry point command for the container
ENTRYPOINT ["java", "-jar", "app.jar"]
