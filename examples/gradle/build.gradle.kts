import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.51.0"
    kotlin("jvm") version "2.1.21"
}

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("net.thauvin.erik:cryptoprice:1.0.3-SNAPSHOT")
    implementation("org.json:json:20240303")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("com.example.CryptoPriceExampleKt")
}

kotlin {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
}

tasks {
    register<JavaExec>("runJava") {
        group = "application"
        mainClass.set("com.example.CryptoPriceSample")
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
