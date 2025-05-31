FROM openjdk:17
WORKDIR app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-boot-coupon.jar
EXPOSE 8080
ENV JAVA_OPTS="-Djava.util.logging.ConsoleHandler.level=ALL -Dfile.encoding=UTF-8"
ENTRYPOINT sh -c "java $JAVA_OPTS -jar spring-boot-coupon.jar --trace"