plugins {
    id("org.springframework.boot") version "2.7.18"
    id ("io.spring.dependency-management") version "1.1.6"
    id ("java")
}

group = "com.portfolio"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.18")
    implementation ("com.h2database:h2:2.2.224")
    implementation ("com.google.guava:guava:33.0.0-jre")
    testImplementation ("org.mockito:mockito-core:3.5.13")
    testImplementation ("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test { useJUnitPlatform() }