# define a first stage with only build deps
FROM maven:3.6.3-jdk-11-slim AS builder

# set workdir
WORKDIR /app

# copy the Project Object Model file
COPY ./pom.xml ./pom.xml

# fetch all dependencies and plugins based on the pom file
RUN mvn dependency:go-offline

# copy your other files
COPY ./src ./src

# build
RUN mvn package && cp target/*.jar ./app.jar

# define a final stage from the runtime image
FROM openjdk:11-jre-slim

# copy only the jar file
COPY --from=builder /app/app.jar / 

# expose port
EXPOSE 8080

# set the startup command to run your binary
CMD ["java", "-jar", "app.jar"]