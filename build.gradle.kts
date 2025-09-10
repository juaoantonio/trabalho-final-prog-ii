import java.util.*

plugins {
    id("java")
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.2.1"
    id("com.github.sherter.google-java-format") version "0.9"
}

group = "br.com.joaobarbosa"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    // Opcional: limita a formatação apenas aos arquivos alterados em relação ao branch main
    ratchetFrom("origin/main")

    format("misc") {
        // define os arquivos para aplicar a formatação "misc"
        target("*.gradle", ".gitattributes", ".gitignore")

        // define os passos de formatação
        trimTrailingWhitespace()
        leadingSpacesToTabs() // ou leadingTabsToSpaces(int) caso prefira espaços
        endWithNewline()
    }

    java {
        // não precisa definir target, o Spotless já infere

        // aplica o Google Java Format com algumas opções extras
        googleJavaFormat("1.17.0")
            .aosp()
            .reflowLongStrings()
            .skipJavadocFormatting()

        // corrige formatação de anotações de tipo
        formatAnnotations()

        // adiciona header de licença automático com ano
        licenseHeader("/* (C)${Calendar.YEAR} */")
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.auth0:java-jwt:4.5.0")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
