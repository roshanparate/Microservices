# Use the Java runtime image for the application
FROM eclipse-temurin:26-jre

# Set the image author metadata
LABEL "org.opencontainers.image.authors"="RMP1991"

# Copy the built Spring Boot jar into the container
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# Run the application when the container starts
ENTRYPOINT ["java","-jar","/accounts-0.0.1-SNAPSHOT.jar"]