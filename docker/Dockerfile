FROM adoptopenjdk/openjdk11:alpine-slim
ADD /user-management-service-*.jar /user-management-service.jar
ARG JAVA_ARGS
ENTRYPOINT ["sh", "-c", "java $JAVA_ARGS -jar user-management-service.jar"]
