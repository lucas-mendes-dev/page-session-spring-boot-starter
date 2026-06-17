plugins {
    id("java")
    `java-library`
    `maven-publish`
    signing
    id("org.springframework.boot") version "4.0.6" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.lucasmendes"
version = "1.0.0"
description = "Isolamento de @SessionAttributes por controller sem conflitos de nome"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.7")
    }
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("jakarta.servlet:jakarta.servlet-api")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("starterPublication") {
            from(components["java"])

            artifactId = "page-session-spring-boot-starter"

            pom {
                name = "Page Session Spring Boot Starter"
                description = project.description
                url = "https://github.com/lucas-mendes-dev/page-session-spring-boot-starter"
                inceptionYear = "2026"

                licenses {
                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }

                developers {
                    developer {
                        id = "lucas-mendes-dev"
                        name = "Lucas Mendes"
                        url = "https://github.com/lucas-mendes-dev"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/lucas-mendes-dev/page-session-spring-boot-starter.git"
                    developerConnection = "scm:git:ssh://github.com/lucas-mendes-dev/page-session-spring-boot-starter.git"
                    url = "https://github.com/lucas-mendes-dev/page-session-spring-boot-starter"
                }
            }
        }
    }

    repositories {
        // GitHub Packages
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lucas-mendes-dev/page-session-spring-boot-starter")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.token") as String?
            }
        }

        // Maven Central (via Sonatype) — descomente quando quiser publicar lá
        // maven {
        //     name = "MavenCentral"
        //     url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        //     credentials {
        //         username = System.getenv("OSSRH_USERNAME")
        //         password = System.getenv("OSSRH_PASSWORD")
        //     }
        // }
    }
}

// Assinatura GPG — necessária para Maven Central, opcional para GitHub Packages
signing {
    // Só assina se a chave GPG estiver disponível (ex: CI/CD)
    setRequired({ gradle.taskGraph.hasTask("publishToMavenCentral") })
    sign(publishing.publications["starterPublication"])
}

tasks.withType<Javadoc> {
    options {
        (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
}
