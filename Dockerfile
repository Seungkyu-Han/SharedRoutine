FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/SharedRoutine-0.0.1-SNAPSHOT.war

WORKDIR /sharedRoutine

COPY ${JAR_FILE} sharedRoutine.jar

EXPOSE 8080

CMD ["java", "-jar", "sharedRoutine.jar"]