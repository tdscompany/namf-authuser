plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("org.owasp.dependencycheck") version "12.1.1"
    id("com.google.cloud.tools.jib") version "3.4.5"
    id("org.sonarqube") version "6.1.0.5360"
}

group = "com.moura"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.hibernate:hibernate-jpamodelgen:6.6.13.Final")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.modelmapper:modelmapper:3.2.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "tdscompany_namf-authuser_343abc31-fd3d-4a03-a5bc-5c0292a80844")
        property("sonar.projectName", "namf-authuser")
    }
}
tasks.sonar {
    dependsOn(tasks.dependencyCheckAnalyze, tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.dependencyCheckAnalyze {
    config.formats = listOf("JSON", "HTML")
    config.nvd.apiKey = project.property("nvd.apiKey") as String
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
    dependsOn(tasks.test)
}
