package net.thauvin.erik.crypto;

import rife.bld.BuildCommand;
import rife.bld.Project;
import rife.bld.extension.CompileKotlinOperation;
import rife.bld.extension.DetektOperation;
import rife.bld.extension.JacocoReportOperation;
import rife.bld.extension.dokka.DokkaOperation;
import rife.bld.extension.dokka.LoggingLevel;
import rife.bld.extension.dokka.OutputFormat;
import rife.bld.operations.exceptions.ExitStatusException;
import rife.bld.publish.PomBuilder;
import rife.bld.publish.PublishDeveloper;
import rife.bld.publish.PublishLicense;
import rife.bld.publish.PublishScm;
import rife.tools.exceptions.FileUtilsErrorException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;
import static rife.bld.dependencies.Scope.test;

public class CryptoPriceBuild extends Project {
    public CryptoPriceBuild() {
        pkg = "net.thauvin.erik.crypto";
        name = "cryptoprice";
        version = version(1, 0, 3, "SNAPSHOT");

        mainClass = "net.thauvin.erik.crypto.CryptoPrice";

        javaRelease = 11;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL);

        final var kotlin = version(1, 9, 22);
        scope(compile)
                .include(dependency("org.jetbrains.kotlin", "kotlin-stdlib", kotlin))
                .include(dependency("org.json", "json", "20240205"))
                .include(dependency("com.squareup.okhttp3", "okhttp", version(4, 12, 0)));
        scope(test)
                .include(dependency("com.willowtreeapps.assertk", "assertk-jvm", version(0, 28, 0)))
                .include(dependency("org.jetbrains.kotlin", "kotlin-test-junit5", kotlin))
                .include(dependency("org.junit.jupiter", "junit-jupiter", version(5, 10, 2)))
                .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1, 10, 2)));

        publishOperation()
                .repository(version.isSnapshot() ? repository(SONATYPE_SNAPSHOTS_LEGACY.location())
                        .withCredentials(property("sonatype.user"), property("sonatype.password"))
                        : repository(SONATYPE_RELEASES_LEGACY.location())
                        .withCredentials(property("sonatype.user"), property("sonatype.password")))
                .info()
                .groupId("net.thauvin.erik")
                .artifactId(name)
                .description("Retrieve cryptocurrencies prices")
                .url("https://github.com/ethauvin/" + name)
                .developer(
                        new PublishDeveloper()
                                .id("ethauvin")
                                .name("Erik C. Thauvin")
                                .email("erik@thauvin.net")
                                .url("https://erik.thauvin.net/")
                )
                .license(
                        new PublishLicense()
                                .name("BSD 3-Clause")
                                .url("https://opensource.org/licenses/BSD-3-Clause")
                )
                .scm(
                        new PublishScm()
                                .connection("scm:git:https://github.com/ethauvin/" + name + ".git")
                                .developerConnection("scm:git:git@github.com:ethauvin/" + name + ".git")
                                .url("https://github.com/ethauvin/" + name)
                )
                .signKey(property("sign.key"))
                .signPassphrase(property("sign.passphrase"));

        jarSourcesOperation().sourceDirectories(new File(srcMainDirectory(), "kotlin"));
    }

    public static void main(final String[] args) {
        new CryptoPriceBuild().start(args);
    }

    @BuildCommand(summary = "Compiles the Kotlin project")
    @Override
    public void compile() throws IOException {
        new CompileKotlinOperation()
                .fromProject(this)
                .execute();
    }

    @BuildCommand(summary = "Checks source with Detekt")
    public void detekt() throws ExitStatusException, IOException, InterruptedException {
        new DetektOperation()
                .fromProject(this)
                .execute();
    }

    @BuildCommand(value = "detekt-baseline", summary = "Creates the Detekt baseline")
    public void detektBaseline() throws ExitStatusException, IOException, InterruptedException {
        new DetektOperation()
                .fromProject(this)
                .baseline("detekt-baseline.xml")
                .createBaseline(true)
                .execute();
    }

    @BuildCommand(summary = "Generates JaCoCo Reports")
    public void jacoco() throws IOException {
        new JacocoReportOperation()
                .fromProject(this)
                .execute();
    }

    @Override
    public void javadoc() throws ExitStatusException, IOException, InterruptedException {
        new DokkaOperation()
                .fromProject(this)
                .loggingLevel(LoggingLevel.INFO)
                .moduleName("CryptoPrice")
                .moduleVersion(version.toString())
                .outputDir(new File(buildDirectory(), "javadoc"))
                .outputFormat(OutputFormat.JAVADOC)
                .execute();
    }

    @Override
    public void publish() throws Exception {
        super.publish();
        pomRoot();
    }

    @BuildCommand(value = "pom-root", summary = "Generates the POM file in the root directory")
    public void pomRoot() throws FileUtilsErrorException {
        PomBuilder.generateInto(publishOperation().fromProject(this).info(), dependencies(),
                new File(workDirectory, "pom.xml"));
    }
}
