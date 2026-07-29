FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml ../../
COPY vellastra-common/pom.xml ../vellastra-common/
COPY vellastra-comment/pom.xml ./
RUN mvn -f ../pom.xml dependency:go-offline -B -pl vellastra-comment -am -q
COPY . ..
RUN mvn -f ../pom.xml package -pl vellastra-comment -am -DskipTests -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
