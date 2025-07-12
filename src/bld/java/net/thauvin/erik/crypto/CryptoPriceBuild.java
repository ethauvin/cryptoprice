/*
 * CryptoPriceBuild.java
 *
 * Copyright 2021-2025 Erik C. Thauvin (erik@thauvin.net)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *   Neither the name of this project nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.thauvin.erik.crypto;

import rife.bld.BuildCommand;
import rife.bld.Project;
import rife.bld.extension.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;
import static rife.bld.dependencies.Scope.test;

public class CryptoPriceBuild extends Project {
    static final String TEST_RESULTS_DIR = "build/test-results/test/";
    final File srcMainKotlin = new File(srcMainDirectory(), "kotlin");

    public CryptoPriceBuild() {
        pkg = "net.thauvin.erik.crypto";
        name = "cryptoprice";
        version = version(1, 0, 3, "SNAPSHOT");

        mainClass = "net.thauvin.erik.crypto.CryptoPrice";

        javaRelease = 11;

        autoDownloadPurge = true;
        downloadSources = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL);

        final var kotlin = version(2, 2, 0);
        scope(compile)
                .include(dependency("org.jetbrains.kotlin", "kotlin-stdlib", kotlin))
                .include(dependency("org.json", "json", "20250517"))
                .include(dependency("com.squareup.okhttp3", "okhttp-jvm", version(5, 1, 0)));
        scope(test)
                .include(dependency("com.willowtreeapps.assertk", "assertk-jvm", version(0, 28, 1)))
                .include(dependency("org.jetbrains.kotlin", "kotlin-test-junit5", kotlin))
                .include(dependency("org.junit.jupiter", "junit-jupiter", version(5, 13, 3)))
                .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1, 13, 3)))
                .include(dependency("org.junit.platform", "junit-platform-launcher", version(1, 13, 3)));

        publishOperation()
                .repository(version.isSnapshot() ? repository(CENTRAL_SNAPSHOTS.location())
                        .withCredentials(property("central.user"), property("central.password"))
                        : repository(CENTRAL_RELEASES.location())
                        .withCredentials(property("central.user"), property("central.password")))
                .repository(repository("github"))
                .info()
                .groupId("net.thauvin.erik")
                .artifactId(name)
                .description("Retrieve cryptocurrencies prices")
                .url("https://github.com/ethauvin/" + name)
                .developer(new PublishDeveloper()
                        .id("ethauvin")
                        .name("Erik C. Thauvin")
                        .email("erik@thauvin.net")
                        .url("https://erik.thauvin.net/")
                )
                .license(new PublishLicense()
                        .name("BSD 3-Clause")
                        .url("https://opensource.org/licenses/BSD-3-Clause")
                )
                .scm(new PublishScm()
                        .connection("scm:git:https://github.com/ethauvin/" + name + ".git")
                        .developerConnection("scm:git:git@github.com:ethauvin/" + name + ".git")
                        .url("https://github.com/ethauvin/" + name)
                )
                .signKey(property("sign.key"))
                .signPassphrase(property("sign.passphrase"));

        jarSourcesOperation().sourceDirectories(srcMainKotlin);
    }

    public static void main(final String[] args) {
        // Enable detailed logging for the extensions
        final var level = Level.ALL;
        final var logger = Logger.getLogger("rife.bld.extension");
        final var consoleHandler = new ConsoleHandler();

        consoleHandler.setLevel(level);
        logger.addHandler(consoleHandler);
        logger.setLevel(level);
        logger.setUseParentHandlers(false);

        new CryptoPriceBuild().start(args);
    }

    @BuildCommand(summary = "Compiles the Kotlin project")
    @Override
    public void compile() throws Exception {
        final var op = new CompileKotlinOperation().fromProject(this);
        op.compileOptions().languageVersion("1.9").verbose(true);
        op.execute();
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

    @Override
    public void publishLocal() throws Exception {
        super.publishLocal();
        pomRoot();
    }

    @BuildCommand(value = "pom-root", summary = "Generates the POM file in the root directory")
    public void pomRoot() throws FileUtilsErrorException {
        PomBuilder.generateInto(publishOperation().fromProject(this).info(), dependencies(),
                new File(workDirectory, "pom.xml"));
    }

    @BuildCommand(summary = "Generates JaCoCo Reports")
    public void jacoco() throws Exception {
        final var op = new JacocoReportOperation().fromProject(this);
        op.testToolOptions("--reports-dir=" + TEST_RESULTS_DIR);

        Exception ex = null;
        try {
            op.execute();
        } catch (Exception e) {
            ex = e;
        }

        renderWithXunitViewer();

        if (ex != null) {
            throw ex;
        }
    }

    private void renderWithXunitViewer() throws Exception {
        final var xunitViewer = new File("/usr/bin/xunit-viewer");
        if (xunitViewer.exists() && xunitViewer.canExecute()) {
            final var reportsDir = "build/reports/tests/test/";

            Files.createDirectories(Path.of(reportsDir));

            new ExecOperation()
                    .fromProject(this)
                    .command(xunitViewer.getPath(), "-r", TEST_RESULTS_DIR, "-o", reportsDir + "index.html")
                    .execute();
        }
    }

    @Override
    public void test() throws Exception {
        final var op = testOperation().fromProject(this);
        op.testToolOptions().reportsDir(new File(TEST_RESULTS_DIR));

        Exception ex = null;
        try {
            op.execute();
        } catch (Exception e) {
            ex = e;
        }

        renderWithXunitViewer();

        if (ex != null) {
            throw ex;
        }
    }
}
