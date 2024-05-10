package com.example;

import rife.bld.BuildCommand;
import rife.bld.extension.CompileKotlinOperation;
import rife.bld.operations.RunOperation;
import rife.bld.BaseProject;

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
                .include(dependency("net.thauvin.erik", "cryptoprice", version(1, 0, 3, "SNAPSHOT")))
                .include(dependency("org.json", "json", "20240303"));
    }

    public static void main(String[] args) {
        new CryptoPriceExampleBuild().start(args);
    }
    
    @Override
    public void compile() throws Exception {
        new CompileKotlinOperation()
                .fromProject(this)
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
