FROM maven
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn clean package -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/messenger-0.0.1-SNAPSHOT.jar"]