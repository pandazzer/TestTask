FROM maven AS builder
WORKDIR /messageApplication/
COPY . .
RUN mvn clean package  -Dmaven.test.skip=true


FROM openjdk:17.0.2-oracle
COPY --from=builder /messageApplication/target/TestTask-0.0.1-SNAPSHOT.jar /messageApplication/TestTask-0.0.1-SNAPSHOT.jar
WORKDIR /messageApplication/
ENTRYPOINT ["java","-jar","TestTask-0.0.1-SNAPSHOT.jar"]
