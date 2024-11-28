import com.google.protobuf.gradle.GenerateProtoTask
import com.google.protobuf.gradle.GenerateProtoTask.PluginOptions
import com.google.protobuf.gradle.id
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jooq.meta.jaxb.Logging
import org.testcontainers.containers.PostgreSQLContainer
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

group = "com.reactive.product"
version = "0.0.1-SNAPSHOT"

plugins {
//	application
	id("org.jetbrains.kotlin.jvm") version "2.0.21"
	id("org.jetbrains.kotlin.plugin.spring") version "2.0.21"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("jacoco")
	id("org.flywaydb.flyway") version "10.17.3"
	id("org.jooq.jooq-codegen-gradle") version "3.19.15"
	id("com.avast.gradle.docker-compose") version "0.17.10"
	id("com.google.protobuf") version "0.9.4"
//	id("com.gradleup.shadow") version "8.3.3"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

buildscript {
	dependencies {
		classpath("org.postgresql:postgresql:42.7.4")
		classpath("org.flywaydb:flyway-database-postgresql:10.17.3")
		classpath("org.testcontainers:postgresql:1.20.3")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")
	implementation("org.springframework.boot:spring-boot-configuration-processor")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.1")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.9.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

	implementation("org.flywaydb:flyway-core:10.17.3")
	implementation("org.flywaydb:flyway-database-postgresql:10.17.3")

	runtimeOnly("org.postgresql:postgresql:42.7.4")
	runtimeOnly("org.postgresql:r2dbc-postgresql")

	implementation("io.grpc:grpc-protobuf:1.68.1")
	implementation("io.grpc:grpc-netty-shaded:1.68.1")
//	implementation("io.grpc:grpc-stub:1.68.1")
	implementation("com.google.protobuf:protobuf-java:4.28.3")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
	implementation("io.grpc:grpc-kotlin-stub:1.4.1")

	implementation("org.jooq:jooq:3.19.15")
	implementation("org.jooq:jooq-meta:3.19.15")
	implementation("org.jooq:jooq-codegen:3.19.15")
	implementation("org.jooq:jooq-kotlin-coroutines:3.19.15")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.apache.kafka:kafka-clients")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	implementation("org.jooq:jool:0.9.15")
	implementation("org.testcontainers:postgresql:1.20.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.21")
	testImplementation("org.junit.jupiter:junit-jupiter")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.21")
	testImplementation("org.testcontainers:junit-jupiter:1.20.3")
	testImplementation("org.testcontainers:postgresql:1.20.3")
	testImplementation("org.testcontainers:r2dbc:1.20.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

//application {
//	mainClass = "com.reactive.product.ProductServiceApplicationKt"
//}

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

tasks.register("tc-start") {
	doLast {
		val db = PostgreSQLContainer("postgres:16-alpine")
			.withDatabaseName("postgres")
		db.start()

		// See https://www.jooq.org/doc/latest/manual/code-generation/codegen-system-properties/
		System.setProperty("jooq.codegen.jdbc.url", db.getJdbcUrl())
		System.setProperty("jooq.codegen.jdbc.username", db.getUsername())
		System.setProperty("jooq.codegen.jdbc.password", db.getPassword())

		System.setProperty("flyway.url", db.getJdbcUrl())
		System.setProperty("flyway.user", db.getUsername())
		System.setProperty("flyway.password", db.getPassword())

		System.setProperty("testcontainer.containerid", db.getContainerId())
		System.setProperty("testcontainer.imageName", db.getDockerImageName())

		Class.forName("org.postgresql.Driver")
	}
}

tasks.register("tc-stop") {
	doLast {
		val containerId = System.getProperty("testcontainer.containerid") as String
		val imageName = System.getProperty("testcontainer.imageName") as String

		println("Stopping testcontainer $containerId - $imageName")
		org.testcontainers.utility.ResourceReaper
			.instance()
			.stopAndRemoveContainer(containerId, imageName);
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

sourceSets.main {
	kotlin.srcDirs(
		"src/main/kotlin",
		"src/generated/jooq",
		"${generatedFilesBaseDir}/main/java",
		"${generatedFilesBaseDir}/main/grpckt"
	)
}

flyway {
	driver = "org.postgresql.Driver"
	locations = arrayOf("filesystem:./src/main/resources/db/migration")
	connectRetries = 15
}

tasks.flywayMigrate {
	dependsOn( "tc-start")
}

jooq {
	configuration {
		logging = Logging.DEBUG
		generator {
			name = "org.jooq.codegen.KotlinGenerator"
			database {
				name = "org.jooq.meta.postgres.PostgresDatabase"
				includes = ".*"
				schemata {
					schema {
						inputSchema = "public"
					}
				}
			}
			generate {
				isGeneratedAnnotation = false
				isRelations = true
				isKotlinNotNullRecordAttributes = true
				isKotlinNotNullInterfaceAttributes = true
				isKotlinDefaultedNullablePojoAttributes = true
			}
			target {
				packageName = "com.reactive.product.database.jooq"
				directory = "src/generated/jooq"
			}
		}
	}
}

tasks.named("jooqCodegen").configure {
	dependsOn( "tc-start")
	dependsOn( "flywayMigrate")
	tasks.findByName("flywayMigrate")?.mustRunAfter("tc-start")
	finalizedBy("tc-stop")

	doLast {
		println("Generating Jooq classes")
	}
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
	dependsOn("generateProto")
	compilerOptions.javaParameters = true
	setSource()
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

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
	maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}

sourceSets {
	create("integrationTest") {
		kotlin {
			srcDir("src/integration-test/kotlin")
			compileClasspath += sourceSets["main"].output + sourceSets["test"].output
			runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
		}
		resources {
			srcDir("src/integration-test/resources")
		}
	}
}

configurations {
	getByName("integrationTestImplementation").extendsFrom(configurations["testImplementation"])
	getByName("integrationTestRuntimeOnly").extendsFrom(configurations["testRuntimeOnly"])
}

tasks {
	val integrationTest by registering(Test::class) {
		description = "Runs the integration tests."
		group = "verification"
		testClassesDirs = sourceSets["integrationTest"].output.classesDirs
		classpath = sourceSets["integrationTest"].runtimeClasspath
		shouldRunAfter("test")

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
	}

	// Make 'check' depend on 'integrationTest' to run both unit and integration tests when running './gradlew check'
	named("check") {
		dependsOn(integrationTest)
	}
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

dockerCompose {
	useComposeFiles = listOf("docker-compose.yaml")
	startedServices = listOf(
		"postgres-reactive-growth",
		"zookeeper-reactive-growth",
		"kafka-reactive-growth",
		"kafka-ui-reactive-growth",
	)

	// this is needed in case of Docker not found error.
	// https://github.com/avast/gradle-docker-compose-plugin/issues/435
	if (DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
		dockerExecutable = "/usr/local/bin/docker"
	} else if (DefaultNativePlatform.getCurrentOperatingSystem().isLinux) {
		dockerExecutable = "/usr/bin/docker"
	} else if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows) {
		dockerExecutable = "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe"
	}
}

tasks.register("prepareAndRun") {
	group = "application"
	description = "Builds the project, starts Postgres with docker-compose, and runs the application."

	// Step 1: Build the project
	dependsOn("build")
	dependsOn("composeUp")
	finalizedBy("bootRun")
}


