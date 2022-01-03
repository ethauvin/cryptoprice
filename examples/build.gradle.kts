import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.40.0"
    kotlin("jvm") version "1.6.10"
}

// ./gradlew run 
// ./gradlew runJava
// ./gradlew run --args="btc"
// ./gradlew runJava --args="eth eur"

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("net.thauvin.erik:cryptoprice:0.9.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("com.example.CryptoPriceExampleKt")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = java.targetCompatibility.toString()
    }

    register<JavaExec>("runJava") {
        group = "application"
        mainClass.set("com.example.CryptoPriceSample")
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
