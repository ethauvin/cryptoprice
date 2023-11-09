package com.example;

import rife.bld.BaseProject;
import rife.bld.BuildCommand;
import rife.bld.extension.CompileKotlinOperation;
import rife.bld.extension.CompileKotlinOptions;
import rife.bld.operations.RunOperation;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;

public class CryptoPriceExampleBuild extends BaseProject {
    public CryptoPriceExampleBuild() {
        pkg = "com.example";
        name = "Example";
        version = version(0, 1, 0);

        mainClass = "com.example.CryptoPriceExampleKt";

        javaRelease = 11;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL, SONATYPE_SNAPSHOTS_LEGACY);

        scope(compile)
                .include(dependency("org.jetbrains.kotlin", "kotlin-stdlib", version(1, 9, 20)))
                .include(dependency("net.thauvin.erik", "cryptoprice", version(1, 0, 1)))
                .include(dependency("org.json", "json", "20231013"));
    }

    public static void main(String[] args) {
        new CryptoPriceExampleBuild().start(args);
    }
    
    @Override
    public void compile() throws Exception {
        new CompileKotlinOperation()
                .fromProject(this)
                .compileOptions(
                        new CompileKotlinOptions()
                                .jdkRelease(javaRelease)
                                .verbose(true)
                )
                .execute();

        // Also compile the Java source code
        super.compile();
    }

    @BuildCommand(value = "run-java", summary = "Runs the Java example")
    public void runJava() throws Exception {
        new RunOperation()
                .fromProject(this)
                .mainClass("com.example.CryptoPriceSample")
                .execute();
    }
}
