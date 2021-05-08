plugins {
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.17.0-RC1"
    kotlin("jvm") version "1.5.0"
    application
}

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

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
    mainClassName = "net.thauvin.erik.crypto.CryptoPrice"
}

detekt {
    toolVersion = "main-SNAPSHOT"
}
