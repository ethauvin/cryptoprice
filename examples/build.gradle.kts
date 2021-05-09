plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    application
}

// ./gradlew run 
// ./gradlew runJava"

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

repositories {
    mavenLocal()
    mavenCentral()
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
        classpath = sourceSets["main"].runtimeClasspath
    }
}
