FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw || true

RUN ./mvnw clean package || mvn clean package

EXPOSE 5000

CMD ["java", "-jar", "target/uazzappserver-1.0-SNAPSHOT.jar"]