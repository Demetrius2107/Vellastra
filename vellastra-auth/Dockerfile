FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml ../../
COPY vellastra-common/pom.xml ../vellastra-common/
COPY vellastra-auth/pom.xml ./
RUN mvn -f ../pom.xml dependency:go-offline -B -pl vellastra-auth -am -q
COPY . ..
RUN mvn -f ../pom.xml package -pl vellastra-auth -am -DskipTests -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
