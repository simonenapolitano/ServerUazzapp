FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package

EXPOSE 5000

CMD ["java", "-jar", "target/uazzappserver-1.0-SNAPSHOT.jar"]
