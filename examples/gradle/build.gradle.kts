import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.53.0"
    kotlin("jvm") version "2.3.0"
}

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
}

dependencies {
    implementation("net.thauvin.erik:cryptoprice:1.0.3-SNAPSHOT")
    implementation("org.json:json:20240303")
}

application {
    mainClass.set("com.example.CryptoPriceExampleKt")
}

tasks {
    register<JavaExec>("runJava") {
        group = "application"
        mainClass.set("com.example.CryptoPriceSample")
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
