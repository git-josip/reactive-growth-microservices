import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.21"
    application

    idea
    jacoco
    id("org.sonarqube") version "5.0.0.4638"
    id("com.gradleup.shadow") version "8.3.3"
}

repositories {
    mavenCentral()
}

val armeriaVersion = "1.30.1"
val brotliVersion = "1.16.0"
val bouncycastleVersion = "1.78.1"

dependencies {
    implementation(platform("com.linecorp.armeria:armeria-bom:$armeriaVersion"))
    implementation("com.linecorp.armeria:armeria-bucket4j")
    implementation("com.linecorp.armeria:armeria-prometheus1")
    implementation("com.linecorp.armeria:armeria")
    implementation("com.linecorp.armeria:armeria-logback")
    implementation("com.linecorp.armeria:armeria-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.aayushatharva.brotli4j:brotli4j:$brotliVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
    implementation("com.bucket4j:bucket4j-core:8.10.1")
    // error on self signed tls
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")
    implementation("org.bouncycastle:bcpkix-jdk18on:$bouncycastleVersion")

    implementation("io.reactivex.rxjava3:rxjava:3.1.9")

    runtimeOnly("org.slf4j:log4j-over-slf4j:2.0.16")
    runtimeOnly("ch.qos.logback:logback-core:1.5.11")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.11")
    runtimeOnly("com.aayushatharva.brotli4j:native-osx-aarch64:$brotliVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("com.linecorp.armeria:armeria-junit5")
}

application {
    mainClass = "com.gradle.develocity.assignment.MyServerKt"
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.compileTestKotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.compileKotlin {
    compilerOptions.javaParameters = true
}

tasks.register<JavaExec>("start") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.gradle.develocity.assignment.MyServerKt")
}

tasks.test {
    maxHeapSize = "2g"
    useJUnitPlatform()

    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )

        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = false
        showExceptions = true
        showCauses = true
        showStackTraces = true

        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}

tasks.withType<JavaCompile>().configureEach {
    options.isFork = true
    options.isIncremental = true
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}