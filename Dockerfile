from openjdk:17-alpine

COPY build/libs/hntoplinks.jar /app/hntoplinks.jar
COPY build/resources/main/application.properties /app/application.properties
#ENTRYPOINT ["java","-jar","/app/hntoplinks.jar"]
ENTRYPOINT ["sh","-c","printenv; java -jar /app/hntoplinks.jar"]
#ENTRYPOINT ["sh","-c","echo $DATABASE_HOST; tail"]
