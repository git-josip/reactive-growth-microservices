import com.google.protobuf.gradle.GenerateProtoTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.0.21"
    application
    idea
    jacoco
    id("org.sonarqube") version "5.0.0.4638"
    id("com.gradleup.shadow") version "8.3.3"
    id("com.google.protobuf") version "0.9.4"
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
    implementation("com.linecorp.armeria:armeria-grpc")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.aayushatharva.brotli4j:brotli4j:$brotliVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
    implementation("com.bucket4j:bucket4j-core:8.10.1")
    // error on self signed tls
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")
    implementation("org.bouncycastle:bcpkix-jdk18on:$bouncycastleVersion")

    implementation("io.reactivex.rxjava3:rxjava:3.1.9")

    implementation("io.grpc:grpc-protobuf:1.68.1")
    implementation("io.grpc:grpc-netty-shaded:1.68.1")
//	implementation("io.grpc:grpc-stub:1.68.1")
    implementation("com.google.protobuf:protobuf-java:4.28.3")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")

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

val generatedFilesBaseDir = "$buildDir/generated/source/proto"
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }

    tasks.getByName("clean") {
        delete(generatedFilesBaseDir)
    }

    generateProtoTasks {
        all().forEach { task: GenerateProtoTask ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }

    generatedFilesBaseDir = generatedFilesBaseDir
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.compileKotlin {
    dependsOn("generateProto")
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
    compilerOptions.javaParameters = true
}

tasks.compileTestKotlin {
    dependsOn("generateProto")
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}


sourceSets.main {
    kotlin.srcDirs(
        "src/main/kotlin",
        "${generatedFilesBaseDir}/main/java",
        "${generatedFilesBaseDir}/main/grpckt"
    )
}


tasks.register<JavaExec>("start") {
    dependsOn("generateProto")
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