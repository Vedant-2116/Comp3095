plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "ca.gbc"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("io.micrometer:micrometer-observation:1.11.3")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.4")
    implementation("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.projectlombok:lombok")
    implementation(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j:3.0.3")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.testcontainers:postgresql:1.18.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.1.4")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
