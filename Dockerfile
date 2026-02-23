FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY task-management.jar app.jar
CMD ["java", "-jar", "app.jar"]