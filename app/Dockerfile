from openjdk:17.0.2

COPY build/libs/hntoplinks.jar /app/hntoplinks.jar
COPY build/resources/main/* /app/

CMD [ "java", "-Dserver.port=${PORT}", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=UTC", "-jar", "-Dspring.profiles.active=${PROFILE}", "/app/hntoplinks.jar" ]

