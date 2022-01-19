from openjdk:17-alpine

COPY build/libs/hntoplinks.jar /app/hntoplinks.jar
COPY build/resources/main/application.properties /app/application.properties

CMD ["java", "-Dserver.port=$PORT", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=UTC", "-jar", "/app/hntoplinks.jar"]

