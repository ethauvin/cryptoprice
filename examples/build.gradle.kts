plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.39.0"
    kotlin("jvm") version "1.5.10"
}

// ./gradlew run 
// ./gradlew runJava

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("net.thauvin.erik:cryptoprice:0.9.0-SNAPSHOT")
}

application {
    mainClass.set("com.example.CryptoPriceExampleKt")
}

tasks {
    register<JavaExec>("runJava") {
        group = "application"
        main = "com.example.CryptoPriceSample"
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
