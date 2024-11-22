import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jooq.meta.jaxb.Logging
import org.testcontainers.containers.PostgreSQLContainer
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

group = "com.finmid"
version = "0.0.1-SNAPSHOT"

plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.spring)
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.dependency.management)
	alias(libs.plugins.jacoco)
	alias(libs.plugins.flyway)
	alias(libs.plugins.jooq.codegen)
	alias(libs.plugins.avast.docker.compose)
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
		classpath(libs.postgresql)
		classpath(libs.flyway.database.postgresql)
		classpath(libs.testcontainers.postgresql)
	}
}

dependencies {
	implementation(libs.spring.boot.starter.actuator)
	implementation(libs.spring.boot.starter.data.r2dbc)
	implementation(libs.spring.boot.starter.jooq)
	implementation(libs.spring.boot.starter.webflux)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.springdoc.openapi.starter.webflux)
	implementation(libs.spring.boot.configuration.processor)

	implementation(libs.jackson.module.kotlin)
	implementation(libs.reactor.kotlin.extensions)
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlinx.coroutines.reactor)
	implementation(libs.kotlinx.coroutines.reactive)
	implementation(libs.kotlinx.coroutines.core)

	implementation(libs.flyway.core)
	implementation(libs.flyway.database.postgresql)

	runtimeOnly(libs.postgresql)
	runtimeOnly(libs.r2dbc.postgresql)


	implementation(libs.jooq)
	implementation(libs.jooq.meta)
	implementation(libs.jooq.codegen)
	implementation(libs.jooq.kotlin.coroutines)
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.apache.kafka:kafka-clients")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	implementation(libs.jool)
	implementation(libs.testcontainers.postgresql)

	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.kotlin.test.junit5)
	testImplementation(libs.junit.jupiter)

	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.spring.boot.testcontainers)
	testImplementation(libs.reactor.test)
	testImplementation(libs.kotlin.test.junit5)
	testImplementation(libs.testcontainers.junit.jupiter)
	testImplementation(libs.testcontainers.postgresql)
	testImplementation(libs.testcontainers.r2dbc)
	testRuntimeOnly(libs.junit.platform.launcher)
	testImplementation(libs.kotlinx.coroutines.test)
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
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
	java.srcDirs(
		"src/main/kotlin",
		"src/generated/jooq"
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
				packageName = "com.finmid.backendinterview.database.jooq"
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
	toolVersion = libs.versions.jacoco.get()
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
	useComposeFiles = listOf("docker/postgres/docker-compose-postgres.yml")
	startedServices = listOf("postgres-finmid")

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


