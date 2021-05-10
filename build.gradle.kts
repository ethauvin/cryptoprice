import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.17.0-RC1"
    id("org.jetbrains.dokka") version "1.4.32"
    id("org.sonarqube") version "3.2.0"
    kotlin("jvm") version "1.5.0"
    application
    `maven-publish`
    jacoco
    java
    signing
}

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

description = "Retrieve cryptocurrencies current market prices."
group = "net.thauvin.erik"
version = "0.9.0-SNAPSHOT"

val deployDir = "deploy"
val gitHub = "ethauvin/$name"
val mavenUrl = "https://github.com/$gitHub"
val publicationName = "mavenJava"

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.json:json:20210307")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("net.thauvin.erik.crypto.CryptoPrice")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}


detekt {
    toolVersion = "main-SNAPSHOT"
}

sonarqube {
    properties {
        property("sonar.projectKey", "ethauvin_$name")
        property("sonar.sourceEncoding", "UTF-8")
    }
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Test> {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    withType<GenerateMavenPom> {
        destination = file("$projectDir/pom.xml")
    }

    jacoco {
        toolVersion = "0.8.7"
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    clean {
        doLast {
            project.delete(fileTree(deployDir))
        }
    }

    withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            named("main") {
                moduleName.set("CryptoPrice")
            }
        }
    }

    "sonarqube" {
        dependsOn("jacocoTestReport")
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            from(components["java"])
            artifact(javadocJar)
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set(mavenUrl)
                licenses {
                    license {
                        name.set("BSD 3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        id.set("ethauvin")
                        name.set("Erik C. Thauvin")
                        email.set("erik@thauvin.net")
                        url.set("https://erik.thauvin.net/")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$gitHub.git")
                    developerConnection.set("scm:git:git@github.com:$gitHub.git")
                    url.set(mavenUrl)
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("$mavenUrl/issues")
                }
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            url = if (project.version.toString().contains("SNAPSHOT"))
                uri("https://oss.sonatype.org/content/repositories/snapshots/") else
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications[publicationName])
}
