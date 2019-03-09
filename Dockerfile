FROM openjdk:8-jdk-alpine

ARG deposit_port=2027

ENV server.max-http-header-size=16384 \
    cassandra.clusterName="Test Cluster" \
    server.port=$deposit_port

WORKDIR /tmp
COPY deposit-account-management-service-boot-0.1.0-BUILD-SNAPSHOT.jar .

CMD ["java", "-jar", "deposit-account-management-service-boot-0.1.0-BUILD-SNAPSHOT.jar"]
