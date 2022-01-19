from openjdk:17-alpine

COPY build/libs/hntoplinks.jar /app/hntoplinks.jar
COPY build/resources/main/application.properties /app/application.properties

# CMD [“java”,”-Djava.security.egd=file:/dev/./urandom”,”-jar”,”/RestApp-0.0.1-SNAPSHOT.jar”]
# ENTRYPOINT ["sh","-c","java -Duser.timezone=UTC -jar /app/hntoplinks.jar"]
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=UTC", "-jar", "/app/hntoplinks.jar"]

