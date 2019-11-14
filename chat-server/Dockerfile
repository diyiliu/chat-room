FROM java:8
VOLUME /tmp
ADD chat-server.jar app.jar
EXPOSE 6600
ENTRYPOINT ["java","-jar","app.jar"]