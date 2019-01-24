FROM java:8
WORKDIR /
ADD config.txt config.txt
ADD target/submitthethird-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
CMD java -jar app.jar config.txt

